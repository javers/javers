package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import com.mongodb.DB
import com.mongodb.DBObject
import org.javers.common.collections.Optional
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.GlobalCdoId
import org.javers.model.DummyProduct
import org.javers.repository.api.JaversRepository
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.repository.mongo.MongoRepositoryFactory.DEFAULT_COLLECTION_NAME

class MongoRepositoryTest extends Specification {


    def "should provide Mongo object"() {

        given:
        DB mongo = new Fongo("myDb").mongo.getDB("test")

        when:
        JaversRepository mongoDiffRepository = new MongoRepository(mongo)

        then:
        mongoDiffRepository.mongo.name == "test"
    }

    //TODO move to int
    def "should persist commit"() {

        given:
        def mongo = new Fongo("myDb").mongo.getDB("test")
        def commitFactory = javersTestAssembly().commitFactory
        def commit = commitFactory.create("charlie", new DummyProduct(1, "Candy"))

        MongoRepository mongoRepository = new MongoRepository(mongo, Stub(CommitMapper))

        when:
        mongoRepository.persist(commit)

        then:
        mongo.getCollection(DEFAULT_COLLECTION_NAME).count() == 1
    }

    def "should get CdoSnapshot from all finded"() {

        given:
        def commitMapper = Stub(CommitMapper) {
            toCdoSnapshots(_ as DBObject) >> {[
                    new CdoSnapshot(Stub(GlobalCdoId){
                        getCdoId() >> 1
                    }, [:]),
                    new CdoSnapshot(Stub(GlobalCdoId){
                        getCdoId() >> 2
                    }, [:])
            ]}
        }

        MongoRepository mongoRepository = new MongoRepository(Stub(DB), commitMapper)

        when:
        Optional<CdoSnapshot> latestSnapshot =  mongoRepository.getLatest(Stub(GlobalCdoId))

        then:
        latestSnapshot.get().globalId.cdoId == 2
    }

    def "should get state history"() {

    }
}