package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import com.mongodb.DB
import com.mongodb.Mongo
import org.javers.common.collections.Optional
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.commit.Commit
import org.javers.core.commit.CommitFactory
import org.javers.core.diff.DiffFactory
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.GlobalCdoId
import org.javers.core.metamodel.object.GlobalIdFactory
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.property.Entity
import org.javers.core.metamodel.type.TypeFactory
import org.javers.core.metamodel.type.TypeMapper
import org.javers.repository.api.JaversRepository
import spock.lang.Ignore
import spock.lang.Specification


class MongoRepositoryTest extends Specification {

    def "should provide Mongo object"() {

        given:
        Mongo mongo = new Fongo("myDb").mongo

        when:
        JaversRepository mongoDiffRepository = new MongoRepository(mongo.getDB("test"))

        then:
        mongoDiffRepository.mongo
    }

    def "should persist commit"() {

        given:
        DummyProduct dummyProduct = new DummyProduct(1, "Candy")

        Javers javers = JaversBuilder.javers()
                .registerEntity(DummyProduct)
                .build()

        Mongo mongo = new Fongo("myDb").mongo
        DB db = mongo.getDB("test")

        Commit commit = javers.commit("charlie", dummyProduct)

        def mongoDiffRepository = new MongoRepository(db)

        when:
        mongoDiffRepository.persist(commit)

        then:
        db.getCollection("Commit").count() == 1
    }

    def "should return empty list for non empty Commit collection"() {

        given:
        GlobalCdoId globalCdoId = Stub()
        globalCdoId.getCdoId() >> 1
        globalCdoId.getCdoClass() >> DummyProduct

        Mongo mongo = new Fongo("myDb").mongo
        def mongoDiffRepository = new MongoRepository(mongo.getDB("db"))

        when:
        def latestSnapshot = mongoDiffRepository.getLatest(globalCdoId)

        then:
        latestSnapshot.empty
    }

    def "should get latest CdoSnapshot"() {

        given:
        DummyProduct dummyProduct = new DummyProduct(1, "Candy")

        Mongo mongo = new Fongo("myDb").mongo
        DB db = mongo.getDB("test")

        def mongoDiffRepository = new MongoRepository(db)

        Javers javers = JaversBuilder.javers()
                .registerEntity(DummyProduct)
                .registerJaversRepository(mongoDiffRepository)
                .build()

        javers.commit("charlie", dummyProduct)

        GlobalCdoId id = Stub() {
            getCdoId() >> 1
            getCdoClass() >> DummyProduct
        }

        when:
        Optional<CdoSnapshot> latestSnapshot =  mongoDiffRepository.getLatest(id)

        then:
        latestSnapshot.isPresent()

    }
}