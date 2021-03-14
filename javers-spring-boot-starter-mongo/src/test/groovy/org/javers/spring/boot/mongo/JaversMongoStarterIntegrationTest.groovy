package org.javers.spring.boot.mongo

import com.mongodb.client.MongoDatabase
import org.javers.core.Javers
import org.javers.core.metamodel.type.EntityType
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
class JaversMongoStarterIntegrationTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build default javers instance with auto-audit aspect" () {
        when:
        println javers.getTypeMapping(DummyEntity).prettyPrint()

        def dummyEntity = dummyEntityRepository.save(new DummyEntity(UUID.randomUUID().hashCode()))
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(dummyEntity.id, DummyEntity).build())

        then:
        assert snapshots.size() == 1
        assert snapshots[0].commitMetadata.properties["dummyEntityId"] == dummyEntity.id + ""
        assert snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should register custom JSON type adapter from spring context"() {
        expect:
        javers.jsonConverter.toJson(new TestApplication.DummyBigDecimalEntity(BigDecimal.TEN)) == '"10"'
    }
}
