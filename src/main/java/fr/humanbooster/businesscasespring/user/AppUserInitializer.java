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
        String username = System.getenv().getOrDefault("APP_ADMIN_USERNAME", "prodadmin");
        String password = System.getenv().getOrDefault("APP_ADMIN_PASSWORD", "ChangeMeStrongPassword123!");
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
}
