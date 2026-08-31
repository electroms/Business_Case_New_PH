package fr.humanbooster.businesscasespring.user;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates or updates the initial application administrator.
 *
 * This component ensures the app does not depend on an in-memory default user and instead
 * uses environment-controlled credentials at startup.
 */
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
        // In production, the administrator credentials are mandatory and must come from the runtime environment.
        // This prevents the application from starting with weak or default credentials in a real deployment.
        String username = isProductionProfile()
            ? requireEnv("APP_ADMIN_USERNAME")
            : getEnvOrDefault("APP_ADMIN_USERNAME", "dev-admin");

        // The local environment uses a stronger-than-default example value to avoid a trivial credential.
        String password = isProductionProfile()
            ? requireEnv("APP_ADMIN_PASSWORD")
            : getEnvOrDefault("APP_ADMIN_PASSWORD", "ChangeMe!DevPassword!2026");

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

    /**
     * Checks whether the runtime profile is set to production.
     *
     * When this is true, a stricter environment-based credential policy is enforced to avoid accidental
     * exposure of insecure startup defaults.
     */
    private boolean isProductionProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.stream(activeProfiles).anyMatch("prod"::equalsIgnoreCase)
            || "prod".equalsIgnoreCase(environment.getProperty("spring.profiles.active"));
    }

    /**
     * Reads a value from the environment and falls back to a safe local default when absent.
     */
    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    /**
     * Enforces required variables in production mode.
     *
     * If a required secret or credential is missing, the application fails fast so the deployment does not
     * start in a partially configured state.
     */
    private String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }
}
