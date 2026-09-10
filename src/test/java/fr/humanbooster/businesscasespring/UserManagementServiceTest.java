package fr.humanbooster.businesscasespring;

import fr.humanbooster.businesscasespring.user.AppUser;
import fr.humanbooster.businesscasespring.user.AppUserRepository;
import fr.humanbooster.businesscasespring.user.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserManagementServiceTest {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanState() {
        appUserRepository.deleteAll();
    }

    @Test
    void createUserShouldPersistAndHashPassword() {
        AppUser admin = new AppUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles("ROLE_ADMIN,ROLE_USER");
        admin.setEnabled(true);
        appUserRepository.save(admin);

        AppUser created = userManagementService.createUser("alice", "secret123", "ROLE_USER", true, "admin");

        assertThat(created.getUsername()).isEqualTo("alice");
        assertThat(created.getPassword()).isNotEqualTo("secret123");
        assertThat(created.getRoles()).contains("ROLE_USER");
        assertThat(appUserRepository.findByUsername("alice")).isPresent();
    }

    @Test
    void deletingCurrentUserShouldBeRejected() {
        AppUser admin = new AppUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles("ROLE_ADMIN,ROLE_USER");
        admin.setEnabled(true);
        appUserRepository.save(admin);

        assertThatThrownBy(() -> userManagementService.deleteUser(admin.getId(), admin.getUsername()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("vous-même");
    }

    @Test
    void deletingLastAdminShouldBeRejected() {
        AppUser admin = new AppUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles("ROLE_ADMIN");
        admin.setEnabled(true);
        appUserRepository.save(admin);

        assertThatThrownBy(() -> userManagementService.deleteUser(admin.getId(), "different-user"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("dernier administrateur");
    }
}
