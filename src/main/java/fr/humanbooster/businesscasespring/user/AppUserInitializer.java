package fr.humanbooster.businesscasespring.user;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppUserInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public AppUserInitializer(AppUserRepository appUserRepository,
                             PasswordEncoder passwordEncoder,
                             Environment environment) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        String username = isProductionProfile()
            ? requireEnv("APP_ADMIN_USERNAME")
            : getEnvOrDefault("APP_ADMIN_USERNAME", "admin");
        String password = isProductionProfile()
            ? requireEnv("APP_ADMIN_PASSWORD")
            : getEnvOrDefault("APP_ADMIN_PASSWORD", "admin");
        String roles = getEnvOrDefault("APP_ADMIN_ROLES", "ROLE_ADMIN,ROLE_USER");

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

    private boolean isProductionProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.stream(activeProfiles).anyMatch("prod"::equalsIgnoreCase)
            || "prod".equalsIgnoreCase(environment.getProperty("spring.profiles.active"));
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }
}
