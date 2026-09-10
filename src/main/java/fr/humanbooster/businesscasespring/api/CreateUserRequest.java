package fr.humanbooster.businesscasespring.api;

public record CreateUserRequest(String username, String password, String roles, boolean enabled) {
}
