package fr.humanbooster.businesscasespring.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppUserInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String username = requireEnv("APP_ADMIN_USERNAME");
        String password = requireEnv("APP_ADMIN_PASSWORD");
        String roles = System.getenv().getOrDefault("APP_ADMIN_ROLES", "ROLE_ADMIN,ROLE_USER");

        appUserRepository.findByUsername(username).ifPresentOrElse(
            user -> {
                user.setPassword(passwordEncoder.encode(password));
                user.setRoles(roles);
                user.setEnabled(true);
                appUserRepository.save(user);
            },
            () -> {
                AppUser user = new AppUser();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setRoles(roles);
                user.setEnabled(true);
                appUserRepository.save(user);
            }
        );
    }

    private String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }
}
