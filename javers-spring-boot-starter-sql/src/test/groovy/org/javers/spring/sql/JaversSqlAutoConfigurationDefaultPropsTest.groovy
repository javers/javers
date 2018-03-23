package org.javers.spring.sql

import org.javers.repository.sql.DialectName
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.javers.spring.boot.TestApplication
import org.javers.spring.boot.sql.JaversSqlProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
class JaversSqlAutoConfigurationDefaultPropsTest extends Specification {

    @Autowired
    DialectName dialectName

    @Autowired
    JaversSqlProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "should provide default props" () {
        expect:
        javersProperties.getAlgorithm() == "simple"
        javersProperties.getMappingStyle() == "field"
        !javersProperties.isNewObjectSnapshot()
        javersProperties.isPrettyPrint()
        !javersProperties.isTypeSafeValues()
        javersProperties.packagesToScan == ""
        dialectName == DialectName.H2
        javersProperties.isSqlSchemaManagementEnabled()
        javersProperties.datePrettyPrintFormats.localDateTimeFormat == "dd M yyyy HH:mm:ss"
        javersProperties.datePrettyPrintFormats.zonedDateTimeFormat == "dd M yyyy HH:mm:ssZ"
        javersProperties.datePrettyPrintFormats.localDateFormat == "dd M yyyy"
        javersProperties.datePrettyPrintFormats.localTimeFormat == "HH:mm:ss"
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath" () {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
