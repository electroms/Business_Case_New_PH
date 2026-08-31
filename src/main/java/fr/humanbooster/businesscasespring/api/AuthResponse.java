package fr.humanbooster.businesscasespring.api;

/**
 * Response returned after a successful authentication.
 *
 * The frontend stores the access token and sends it in the Authorization header on subsequent protected
 * requests. The token type is usually "Bearer" and expiresIn is expressed in milliseconds.
 */
public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
}
