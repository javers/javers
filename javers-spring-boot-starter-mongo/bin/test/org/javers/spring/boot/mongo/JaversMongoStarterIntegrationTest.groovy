package org.javers.spring.boot.mongo

import org.javers.core.Javers
import org.javers.core.metamodel.type.EntityType
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class JaversMongoStarterIntegrationTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build default javers instance with auto-audit aspect" () {
        when:
        def dummyEntity = dummyEntityRepository.save(new DummyEntity(UUID.randomUUID().hashCode()))
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(dummyEntity.id, DummyEntity).build())

        then:
        assert snapshots.size() == 1
        assert snapshots[0].commitMetadata.properties["dummyEntityId"] == dummyEntity.id + ""
        assert snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should scan given packages for classes with @TypeName"() {
        expect:
        javers.getTypeMapping("AnotherEntity") instanceof EntityType
    }
}