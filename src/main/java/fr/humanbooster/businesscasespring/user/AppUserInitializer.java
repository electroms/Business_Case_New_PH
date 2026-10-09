package fr.humanbooster.businesscasespring.user;

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
        String username = requireProperty("spring.security.user.name");
        String password = requireProperty("spring.security.user.password");
        String roles = environment.getProperty("spring.security.user.roles", "ROLE_ADMIN,ROLE_USER");

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

    private String requireProperty(String key) {
        String value = environment.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration property: " + key);
        }
        return value;
    }
}
