package org.javers.spring.sql;

import org.javers.core.Javers
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.JaversType
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.sql.JaversProperties;
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.fest.assertions.api.Assertions.assertThat

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author pawelszymczyk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [TestApplication.class])
@ActiveProfiles("test")
public class JaversSqlAutoConfigurationTest {

    @Autowired
    Javers javers

    @Autowired
    JaversProperties javersProperties;

    @Test
    void shouldAutowiredJaversInstance() {
        //given
        DummyEntity dummyEntity = new DummyEntity(1);

        //when
        javers.commit("pawel", dummyEntity);

        //then
        assertThat(javers.findSnapshots(QueryBuilder.byClass(DummyEntity).build())).hasSize(1)
    }

    @Test
    void shouldReadConfigurationFromYml() {
        assertThat(javersProperties.getAlgorithm()).isEqualTo("levenshtein_distance")
        assertThat(javersProperties.getMappingStyle()).isEqualTo("bean")
        assertThat(javersProperties.isNewObjectSnapshot()).isFalse()
        assertThat(javersProperties.isPrettyPrint()).isFalse()
        assertThat(javersProperties.isTypeSafeValues()).isTrue()
    }

    @Test
    void shouldReadBeanMappingStyleFromYml() {
        JaversType mappingType = javers.getTypeMapping(DummyEntity)

        assertThat(mappingType).isInstanceOf(EntityType)
    }
}



