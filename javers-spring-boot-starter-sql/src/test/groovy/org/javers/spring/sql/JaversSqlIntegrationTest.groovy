package org.javers.spring.sql

import org.javers.core.Javers
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.JaversType
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.sql.DummyEntity
import org.javers.spring.boot.sql.DummyEntityRepository
import org.javers.spring.boot.sql.TestApplication
import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author pawelszymczyk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [TestApplication.class])
@ActiveProfiles("test")
public class JaversSqlIntegrationTest {

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    @Test
    void shouldAutowiredJaversInstance() {
        //given
        DummyEntity dummyEntity = new DummyEntity(1, "kaz");

        //when
        dummyEntityRepository.save(dummyEntity)

        //then
        assertThat(javers.findSnapshots(QueryBuilder.byClass(DummyEntity).build())).hasSize(1)
    }

    @Test
    void shouldReadBeanMappingStyleFromYml() {
        JaversType mappingType = javers.getTypeMapping(DummyEntity)

        assertThat(mappingType).isInstanceOf(EntityType)
    }
}
