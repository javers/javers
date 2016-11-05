package org.javers.spring.boot.mongo

import org.javers.core.Javers
import org.javers.core.metamodel.type.EntityType
import org.javers.repository.jql.QueryBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner)
@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
public class JaversMongoStarterIntegrationTest {

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    @Test
    void "should build default javers instance with auto-audit aspect"() {
        //given
        def dummyEntity = new DummyEntity(1)

        //when
        dummyEntityRepository.save(dummyEntity)

        //then
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(DummyEntity).build())
        assert snapshots.size() == 1
        assert snapshots[0].commitMetadata.properties["key"] == "ok"
        assert snapshots[0].commitMetadata.author == "unauthenticated"
    }

    @Test
    void "should scan given packages for classes with @TypeName"() {
        //expect
        assert javers.getTypeMapping("AnotherEntity") instanceof EntityType
    }
}