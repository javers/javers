package org.javers.spring.boot.mongo

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired

abstract class JaversMongoStarterDedicatedMongoTest extends BaseSpecification {

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should connect to Mongo configured with javers.mongodb properties"() {
        given:
        MongoDatabase dedicatedDb = MongoClients
                .create("mongodb://localhost:$mongoPort")
                .getDatabase("javers-dedicated")
        dedicatedDb.getCollection("jv_snapshots").drop()

        when:
        def dummyEntity = new DummyEntity(UUID.randomUUID().hashCode())
        javers.commit("a", dummyEntity)
        def snapshots = javers.findSnapshots(QueryBuilder.byInstance(dummyEntity).build())

        then:
        snapshots.size() == 1
        dedicatedDb.getCollection("jv_snapshots").countDocuments() == 1
    }
}
