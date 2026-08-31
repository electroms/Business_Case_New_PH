package fr.humanbooster.businesscasespring.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Exposes the authentication endpoint used by the Angular frontend.
 *
 * The login endpoint validates the supplied credentials and returns a JWT signed with the
 * application's configured secret and expiration time.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Spring Security component in charge of checking the submitted username/password pair.
    private final AuthenticationManager authenticationManager;

    // JWT generator used to issue signed access tokens after a successful login.
    private final JwtEncoder jwtEncoder;

    // Lifetime of the token in milliseconds. This value is usually controlled by the environment.
    private final long expirationMs;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtEncoder jwtEncoder,
                          @Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.expirationMs = expirationMs;
    }

    /**
     * Authenticates the user and returns a signed JWT that can be used in subsequent requests.
     *
     * The login flow validates the credentials through Spring Security, then builds a token containing the
     * subject and role claims. This token is then sent back to the frontend for use on protected API routes.
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("businesscase-spring")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationMs / 1000))
                .subject(authentication.getName())
                .claim("roles", authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList())
                .build();

            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            return new AuthResponse(token, "Bearer", expirationMs);
        } catch (AuthenticationException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED,
                "Identifiants invalides"
            );
        }
    }
}
