package fr.humanbooster.businesscasespring.api;

/**
 * Incoming request payload received by the login endpoint.
 *
 * The frontend sends the username and password entered by the user. Spring validates these credentials
 * against the database-backed authentication manager before issuing a JWT.
 */
public record LoginRequest(String username, String password) {
}
