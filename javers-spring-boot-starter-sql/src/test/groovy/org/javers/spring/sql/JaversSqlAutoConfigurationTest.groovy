package org.javers.spring.sql

import org.javers.core.CommitIdGenerator
import org.javers.core.Javers
import org.javers.core.MappingStyle
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.repository.sql.DialectName
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.javers.spring.boot.sql.JaversSqlProperties
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

    @Autowired Javers javers

    @Autowired
    DialectName dialectName

    @Autowired
    JaversSqlProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "should read configuration from yml" () {
        expect:
        javers.coreConfiguration.listCompareAlgorithm == ListCompareAlgorithm.LEVENSHTEIN_DISTANCE
        javers.coreConfiguration.mappingStyle == MappingStyle.BEAN
       !javers.coreConfiguration.initialChanges
       !javers.coreConfiguration.terminalChanges
       !javers.coreConfiguration.prettyPrint
        javers.coreConfiguration.commitIdGenerator == CommitIdGenerator.RANDOM
       !javers.coreConfiguration.usePrimitiveDefaults

        javersProperties.typeSafeValues
        javersProperties.packagesToScan == "my.company.domain.person, my.company.domain.finance"
       !javersProperties.auditableAspectEnabled
       !javersProperties.springDataAuditableRepositoryAspectEnabled
        javersProperties.prettyPrintDateFormats.localDateTime == "dd-mm-yyyy"
        javersProperties.prettyPrintDateFormats.zonedDateTime == "dd-mm-yyyy HH mm ss Z"
        javersProperties.prettyPrintDateFormats.localDate == "dd-mm-yyyy"
        javersProperties.prettyPrintDateFormats.localTime == "HH mm ss"

        dialectName == DialectName.H2
        javersProperties.sqlSchema == "test"
        javersProperties.sqlSchemaManagementEnabled
        javersProperties.sqlGlobalIdCacheDisabled
        javersProperties.objectAccessHook == "org.javers.spring.boot.DummySqlObjectAccessHook"
        javersProperties.sqlGlobalIdTableName == "cust_jv_global_id"
        javersProperties.sqlCommitTableName == "cust_jv_commit"
        javersProperties.sqlSnapshotTableName == "cust_jv_snapshot"
        javersProperties.sqlCommitPropertyTableName == "cust_jv_commit_property"
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath" () {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
