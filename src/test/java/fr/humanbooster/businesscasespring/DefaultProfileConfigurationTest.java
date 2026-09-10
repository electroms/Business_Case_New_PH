package fr.humanbooster.businesscasespring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
class DefaultProfileConfigurationTest {

    @Autowired
    private Environment environment;

    @Test
    void defaultProfileShouldProvideLocalDatabaseAndStrongJwtSecret() {
        assertThat(environment.getProperty("spring.datasource.url")).contains("jdbc:h2:mem:");
        assertThat(environment.getProperty("spring.datasource.driver-class-name")).isEqualTo("org.h2.Driver");
        assertThat(environment.getProperty("app.jwt.secret")).hasSizeGreaterThanOrEqualTo(32);
    }
}
