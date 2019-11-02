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

    def "should provide default props"() {
        expect:
        javersProperties.getAlgorithm() == "simple"
        javersProperties.getMappingStyle() == "field"
        !javersProperties.isNewObjectSnapshot()
        javersProperties.isPrettyPrint()
        !javersProperties.isTypeSafeValues()
        javersProperties.packagesToScan == ""
        dialectName == DialectName.H2
        javersProperties.sqlSchema == null
        javersProperties.sqlSchemaManagementEnabled
        javersProperties.commitIdGenerator == "synchronized_sequence"
        javersProperties.prettyPrintDateFormats.localDateTime == "dd MMM yyyy, HH:mm:ss"
        javersProperties.prettyPrintDateFormats.zonedDateTime == "dd MMM yyyy, HH:mm:ssZ"
        javersProperties.prettyPrintDateFormats.localDate == "dd MMM yyyy"
        javersProperties.prettyPrintDateFormats.localTime == "HH:mm:ss"
        !javersProperties.sqlGlobalIdCacheDisabled
        javersProperties.objectAccessHook == "org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook"
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
