package fr.humanbooster.businesscasespring;

import fr.humanbooster.businesscasespring.user.AppUser;
import fr.humanbooster.businesscasespring.user.AppUserRepository;
import fr.humanbooster.businesscasespring.user.DatabaseUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseUserDetailsServiceTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DatabaseUserDetailsService databaseUserDetailsService;

    @Test
    void loadsPersistedUser() {
        AppUser user = new AppUser();
        user.setUsername("alice");
        user.setPassword("$2a$10$Q6kA2w7K1mYb6qP2hXQ2OeXxN9Jz4X5B7wC6fI4o8zSx2w1v0Q8.");
        user.setRoles("ROLE_USER");
        user.setEnabled(true);
        appUserRepository.save(user);

        UserDetails loaded = databaseUserDetailsService.loadUserByUsername("alice");

        assertThat(loaded.getUsername()).isEqualTo("alice");
        assertThat(loaded.getAuthorities()).extracting("authority").contains("ROLE_USER");
    }
}
