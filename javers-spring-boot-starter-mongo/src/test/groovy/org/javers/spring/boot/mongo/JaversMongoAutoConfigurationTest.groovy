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
@ActiveProfiles("test")
class JaversMongoAutoConfigurationTest extends Specification{

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
        javersProperties.algorithm == "levenshtein_distance"
        javersProperties.mappingStyle == "bean"
        !javersProperties.newObjectSnapshot
        !javersProperties.prettyPrint
        javersProperties.typeSafeValues
    }

    def "shouldReadBeanMappingStyleFromYml"() {
        expect:
        javers.getTypeMapping(DummyEntity) instanceof EntityType
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
