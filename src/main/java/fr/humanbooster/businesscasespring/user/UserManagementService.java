package fr.humanbooster.businesscasespring.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class UserManagementService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser createUser(String username, String rawPassword, String rawRoles, boolean enabled, String currentUsername) {
        validateUsername(username);
        validatePassword(rawPassword);
        validateRoleInput(rawRoles);

        if (appUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec ce nom existe déjà.");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(normalizeRoles(rawRoles));
        user.setEnabled(enabled);

        AppUser saved = appUserRepository.save(user);

        if (isAdmin(currentUsername) && !isAdmin(saved.getUsername())) {
            return saved;
        }

        return saved;
    }

    @Transactional
    public void deleteUser(Long userId, String currentUsername) {
        AppUser target = appUserRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        if (target.getUsername().equals(currentUsername)) {
            throw new IllegalStateException("Vous ne pouvez pas supprimer vous-même votre compte.");
        }

        boolean isTargetAdmin = hasRole(target, "ROLE_ADMIN");
        long adminCount = appUserRepository.findAll().stream()
            .filter(user -> hasRole(user, "ROLE_ADMIN"))
            .count();

        if (isTargetAdmin && adminCount <= 1) {
            throw new IllegalStateException("Impossible de supprimer le dernier administrateur.");
        }

        appUserRepository.delete(target);
    }

    public List<AppUser> listUsers() {
        return appUserRepository.findAll();
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank() || username.length() < 3) {
            throw new IllegalArgumentException("Le nom d'utilisateur doit contenir au moins 3 caractères.");
        }
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
    }

    private void validateRoleInput(String rawRoles) {
        if (rawRoles == null || rawRoles.isBlank()) {
            throw new IllegalArgumentException("Au moins un rôle doit être défini.");
        }
    }

    private boolean isAdmin(String username) {
        return appUserRepository.findByUsername(username)
            .map(user -> hasRole(user, "ROLE_ADMIN"))
            .orElse(false);
    }

    private boolean hasRole(AppUser user, String role) {
        return Arrays.stream(normalizeRoles(user.getRoles()).split(","))
            .map(String::trim)
            .anyMatch(r -> r.equalsIgnoreCase(role));
    }

    private String normalizeRoles(String rawRoles) {
        if (rawRoles == null || rawRoles.isBlank()) {
            return "ROLE_USER";
        }

        return Arrays.stream(rawRoles.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .map(value -> value.startsWith("ROLE_") ? value : "ROLE_" + value)
            .distinct()
            .reduce((left, right) -> left + "," + right)
            .orElse("ROLE_USER");
    }
}
