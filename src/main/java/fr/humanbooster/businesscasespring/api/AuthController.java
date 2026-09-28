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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Exposes the authentication endpoint used by the Angular frontend.
 *
 * The login endpoint validates the supplied credentials and returns a JWT signed with the
 * application's configured secret and expiration time.
 */
/**
 * Contrôleur d'authentification.
 *
 * Ce point d'entrée est appelé par le frontend lors d'une tentative de connexion.
 * Il valide les identifiants avec Spring Security puis retourne un JWT pour les
 * appels API suivants.
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

    // Basic in-memory throttling to slow credential stuffing attempts.
    private final Map<String, Long> failedLoginTimestamps = new ConcurrentHashMap<>();
    private static final long LOCKOUT_WINDOW_MS = 60000L;
    private static final int MAX_FAILED_ATTEMPTS = 5;

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
        String username = request.username() == null ? "" : request.username().trim();
        if (username.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED,
                "Identifiants invalides"
            );
        }

        long now = System.currentTimeMillis();
        Long lastFailure = failedLoginTimestamps.get(username);
        if (lastFailure != null && (now - lastFailure) < LOCKOUT_WINDOW_MS) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.TOO_MANY_REQUESTS,
                "Trop de tentatives. Réessayez plus tard."
            );
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password())
            );
            failedLoginTimestamps.remove(username);

            Instant nowInstant = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("businesscase-spring")
                .issuedAt(nowInstant)
                .expiresAt(nowInstant.plusSeconds(expirationMs / 1000))
                .subject(authentication.getName())
                .claim("roles", authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList())
                .build();

            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            return new AuthResponse(token, "Bearer", expirationMs);
        } catch (AuthenticationException ex) {
            long failures = failedLoginTimestamps.getOrDefault(username, 0L);
            failedLoginTimestamps.put(username, now);
            if (failures >= MAX_FAILED_ATTEMPTS) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.TOO_MANY_REQUESTS,
                    "Trop de tentatives. Réessayez plus tard."
                );
            }
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED,
                "Identifiants invalides"
            );
        }
    }
}
