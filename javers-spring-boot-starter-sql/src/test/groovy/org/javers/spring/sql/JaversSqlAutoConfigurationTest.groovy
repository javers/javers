package org.javers.spring.sql

import org.javers.repository.sql.DialectName
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.javers.spring.boot.sql.JaversProperties
import org.javers.spring.boot.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class JaversSqlAutoConfigurationTest extends Specification {

    @Autowired
    DialectName dialectName

    @Autowired
    JaversProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "should read configuration from yml" () {
        expect:
        javersProperties.getAlgorithm() == "levenshtein_distance"
        javersProperties.getMappingStyle() == "bean"
        !javersProperties.isNewObjectSnapshot()
        !javersProperties.isPrettyPrint()
        javersProperties.isTypeSafeValues()
        dialectName == DialectName.H2
        !javersProperties.isSqlSchemaManagementEnabled()
        javersProperties.packagesToScan == "my.company.domain.person, my.company.domain.finance"
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath" () {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
