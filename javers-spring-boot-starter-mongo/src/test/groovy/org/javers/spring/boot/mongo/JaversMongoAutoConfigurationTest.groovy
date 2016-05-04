package org.javers.spring.boot.mongo

import org.javers.core.Javers
import org.javers.core.metamodel.type.EntityType
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * @author pawelszymczyk
 */
@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = [TestApplication])
@ActiveProfiles("test")
class JaversMongoAutoConfigurationTest {

    @Autowired
    Javers javers

    @Autowired
    JaversProperties javersProperties

    @Autowired
    AuthorProvider provider

    @Test
    void shouldAutowireJaversInstance() {
        //given
        def dummyEntity = new DummyEntity(1)

        //when
        javers.commit("pawel", dummyEntity)

        //then
        assert javers.findSnapshots(QueryBuilder.byClass(DummyEntity).build()).size() == 1
    }

    @Test
    void shoudUseDbNameFromMongoStarter(){
        assert javers.repository.delegate.mongo.name == "spring-mongo"
    }

    @Test
    void shouldReadConfigurationFromYml() {
        assert javersProperties.algorithm == "levenshtein_distance"
        assert javersProperties.mappingStyle == "bean"
        assert !javersProperties.newObjectSnapshot
        assert !javersProperties.prettyPrint
        assert javersProperties.typeSafeValues
    }

    @Test
    void shouldReadBeanMappingStyleFromYml() {
        assert javers.getTypeMapping(DummyEntity) instanceof EntityType
    }

    @Test
    void shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath() {
        assert provider instanceof SpringSecurityAuthorProvider
    }

}
