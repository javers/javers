package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import com.mongodb.DB
import com.mongodb.Mongo
import org.javers.common.collections.Optional
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.JaversTestBuilder
import org.javers.core.commit.CommitFactory
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.GlobalCdoId
import org.javers.repository.api.JaversRepository
import spock.lang.Shared
import spock.lang.Specification

class MongoRepositoryTest extends Specification {

    @Shared JaversTestBuilder javersTestBuilder
    @Shared MongoRepository mongoDiffRepository
    @Shared CommitFactory commitFactory
    @Shared DB db

    def setupSpec() {
        javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        commitFactory = javersTestBuilder.commitFactory

        Mongo mongo = new Fongo("myDb").mongo
        db = mongo.getDB("db")
        mongoDiffRepository = new MongoRepository(db)
    }


    def "should provide Mongo object"() {

        given:
        Mongo mongo = new Fongo("myDb").mongo

        when:
        JaversRepository mongoDiffRepository = new MongoRepository(mongo.getDB("test"))

        then:
        mongoDiffRepository.mongo.name == "test"
    }

    def "should persist commit"() {

        given:
        DummyProduct dummyProduct = new DummyProduct(1, "Candy")
        def commit = commitFactory.create("charlie", dummyProduct)

        when:
        mongoDiffRepository.persist(commit)

        then:
        db.getCollection("Commit").count() == 1
    }

    def "should return empty list for non empty Commit collection"() {

        given:
        GlobalCdoId globalCdoId = Stub() {
            getCdoId() >> 1
            getCdoClass() >> DummyProduct
        }

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

    def "should get state history"() {

    }
}