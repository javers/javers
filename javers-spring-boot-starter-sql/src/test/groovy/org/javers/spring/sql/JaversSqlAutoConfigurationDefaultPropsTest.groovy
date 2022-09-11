package org.javers.spring.sql

import org.javers.core.CommitIdGenerator
import org.javers.core.Javers
import org.javers.core.MappingStyle
import org.javers.core.diff.ListCompareAlgorithm
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

    @Autowired Javers javers

    @Autowired
    DialectName dialectName

    @Autowired
    JaversSqlProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "should provide default props"() {
        expect:
        javers.coreConfiguration.listCompareAlgorithm == ListCompareAlgorithm.SIMPLE
        javers.coreConfiguration.mappingStyle == MappingStyle.FIELD
        javers.coreConfiguration.initialChanges
        javers.coreConfiguration.terminalChanges
        javers.coreConfiguration.prettyPrint
        javers.coreConfiguration.commitIdGenerator == CommitIdGenerator.SYNCHRONIZED_SEQUENCE
        javers.coreConfiguration.usePrimitiveDefaults

        javersProperties.auditableAspectEnabled
        javersProperties.springDataAuditableRepositoryAspectEnabled
        !javersProperties.isTypeSafeValues()
        javersProperties.packagesToScan == ""
        dialectName == DialectName.H2
        javersProperties.sqlSchema == null
        javersProperties.sqlSchemaManagementEnabled
        javersProperties.prettyPrintDateFormats.localDateTime == "dd MMM yyyy, HH:mm:ss"
        javersProperties.prettyPrintDateFormats.zonedDateTime == "dd MMM yyyy, HH:mm:ssZ"
        javersProperties.prettyPrintDateFormats.localDate == "dd MMM yyyy"
        javersProperties.prettyPrintDateFormats.localTime == "HH:mm:ss"
        !javersProperties.sqlGlobalIdCacheDisabled
        javersProperties.objectAccessHook == "org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook"
        javersProperties.sqlGlobalIdTableName == null
        javersProperties.sqlCommitTableName == null
        javersProperties.sqlSnapshotTableName == null
        javersProperties.sqlCommitPropertyTableName == null
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
