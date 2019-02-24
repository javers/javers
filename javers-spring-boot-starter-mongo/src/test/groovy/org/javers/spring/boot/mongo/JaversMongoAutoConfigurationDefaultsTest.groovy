package org.javers.spring.boot.mongo

import org.javers.core.Javers
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
class JaversMongoAutoConfigurationDefaultsTest extends Specification{

    @Autowired
    JaversMongoProperties javersProperties

    def "should provide default configuration"() {
        expect:
        javersProperties.algorithm == "simple"
        javersProperties.mappingStyle == "field"
       !javersProperties.newObjectSnapshot
        javersProperties.prettyPrint
       !javersProperties.typeSafeValues
        javersProperties.commitIdGenerator == "synchronized_sequence"
       !javersProperties.documentDbCompatibilityEnabled
        javersProperties.auditableAspectEnabled
        javersProperties.springDataAuditableRepositoryAspectEnabled
        javersProperties.packagesToScan == ""
    }
}
