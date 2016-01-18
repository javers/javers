package org.javers.spring.sql

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
    JaversProperties javersProperties;

    @Test
    void shouldReadConfigurationFromYml() {
        assertThat(javersProperties.getAlgorithm()).isEqualTo("levenshtein_distance")
        assertThat(javersProperties.getMappingStyle()).isEqualTo("bean")
        assertThat(javersProperties.isNewObjectSnapshot()).isFalse()
        assertThat(javersProperties.isPrettyPrint()).isFalse()
        assertThat(javersProperties.isTypeSafeValues()).isTrue()
    }
}