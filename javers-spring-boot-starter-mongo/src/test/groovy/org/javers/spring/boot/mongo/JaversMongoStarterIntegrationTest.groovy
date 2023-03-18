package org.javers.spring.boot.mongo

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired

class JaversMongoStarterIntegrationTest extends BaseSpecification {

    class ValueObject {
        BigDecimal value
    }

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

    def "should allow javers customization from spring context" () {
        expect:
        javers.compare(new ValueObject(value: 1.123), new ValueObject(value: 1.124)).changes.size() == 0
    }
}
