package org.javers.spring.boot.mongo

import org.javers.core.CommitIdGenerator
import org.javers.core.Javers
import org.javers.core.MappingStyle
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.metamodel.type.EntityType
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class JaversMongoStarterTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    JaversMongoProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "shoudUseDbNameFromMongoStarter"(){
        expect:
        javers.repository.delegate.mongoSchemaManager.mongo.name == "spring-mongo"
    }

    def "shouldReadConfigurationFromYml"() {
        expect:
        javers.coreConfiguration.listCompareAlgorithm == ListCompareAlgorithm.LEVENSHTEIN_DISTANCE
        javers.coreConfiguration.mappingStyle == MappingStyle.BEAN
       !javers.coreConfiguration.initialChanges
       !javers.coreConfiguration.terminalChanges
       !javers.coreConfiguration.prettyPrint
        javers.coreConfiguration.commitIdGenerator == CommitIdGenerator.RANDOM

        javersProperties.typeSafeValues
        javersProperties.packagesToScan == "org.javers.spring.boot"
        javersProperties.documentDbCompatibilityEnabled == true
       !javersProperties.auditableAspectEnabled
       !javersProperties.springDataAuditableRepositoryAspectEnabled
        javersProperties.prettyPrintDateFormats.localDateTime == "dd-mm-yyyy"
        javersProperties.prettyPrintDateFormats.zonedDateTime == "dd-mm-yyyy HH mm ss Z"
        javersProperties.prettyPrintDateFormats.localDate == "dd-mm-yyyy"
        javersProperties.prettyPrintDateFormats.localTime == "HH mm ss"

        javersProperties.objectAccessHook == "org.javers.spring.boot.mongo.DummyDBRefUnproxyObjectAccessHook"
        javersProperties.snapshotsCacheSize == 100
    }

    def "should scan given packages for classes with @TypeName"() {
        expect:
        javers.getTypeMapping("AnotherEntity") instanceof EntityType
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
