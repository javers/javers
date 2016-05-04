package org.javers.spring.sql

import org.javers.repository.sql.DialectName
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.javers.spring.boot.sql.JaversProperties
import org.javers.spring.boot.sql.TestApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.fest.assertions.api.Assertions.assertThat

/**
 * @author pawelszymczyk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [TestApplication.class])
@ActiveProfiles("integrationTest")
public class JaversSqlAutoConfigurationTest {

    @Autowired
    DialectName dialectName;

    @Autowired
    JaversProperties javersProperties;

    @Autowired
    AuthorProvider provider

    @Test
    void shouldReadConfigurationFromYml() {
        assertThat(javersProperties.getAlgorithm()).isEqualTo("levenshtein_distance")
        assertThat(javersProperties.getMappingStyle()).isEqualTo("bean")
        assertThat(javersProperties.isNewObjectSnapshot()).isFalse()
        assertThat(javersProperties.isPrettyPrint()).isFalse()
        assertThat(javersProperties.isTypeSafeValues()).isTrue()
        assertThat(dialectName).isEqualTo(DialectName.H2)
    }

    @Test
    void shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath() {
        assert provider instanceof SpringSecurityAuthorProvider
    }
}
