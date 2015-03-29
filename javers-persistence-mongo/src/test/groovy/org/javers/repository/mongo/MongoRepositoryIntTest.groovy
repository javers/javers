package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import org.javers.core.JaversTestBuilder
import org.javers.core.json.JsonConverter
import org.javers.core.model.DummyUser
import org.javers.test.builder.DummyUserBuilder
import spock.lang.Specification

import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author pawel szymczyk
 */
class MongoRepositoryIntTest extends Specification {

    def "should persist head id"() {

        given:
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db, javersTestBuilder.jsonConverter)

        def kazikV1 = dummyUser("Kazik").withAge(1).build()
        def kazikV2 = dummyUser("Kazik").withAge(2).build()

        def commit1 = javersTestBuilder.commitFactory.create("author", kazikV1)
        def commit2 = javersTestBuilder.commitFactory.create("author", kazikV2)

        when:
        mongoRepository.persist(commit1)

        then:
        mongoRepository.getHeadId().getMajorId() == 1
        mongoRepository.getHeadId().getMinorId() == 0

        when:
        mongoRepository.persist(commit2)

        then:
        mongoRepository.getHeadId().getMajorId() == 1
        mongoRepository.getHeadId().getMinorId() == 1
    }

    def "should persist commit and get latest snapshot"() {

        given:
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory

        def kazik = new DummyUser("kazik")
        def id = javersTestBuilder.javers().idBuilder().instanceId(new DummyUser("kazik"))

        when:
        //persist
        mongoRepository.persist(commitFactory.create("andy", kazik))

        //get last snapshot
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId == id
        latest.get().size() == 1
    }

    def "should get last commit by GlobalId"() {

        given:
        //create components
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory
        def id = javersTestBuilder.globalIdFactory.createFromId("kazik", DummyUser)

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.cdoClass.clientsClass == DummyUser
    }

    def "should get last commit by InstanceIdDTO"() {

        given:
        //create components
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory
        def id = javersTestBuilder.javers().idBuilder().instanceId(new DummyUser("kazik"))

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.cdoClass.clientsClass == DummyUser
    }

    def "should get state history"() {

        given:
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db, Stub(JsonConverter))

        def javersTestBuilder = JaversTestBuilder.javersTestAssembly(mongoRepository)

        mongoRepository.setJsonConverter(javersTestBuilder.jsonConverter)

        def javers = javersTestBuilder.javers()

        def kazikV1 = DummyUserBuilder.dummyUser("kazik").withAge(12).build()
        def kazikV2 = DummyUserBuilder.dummyUser("kazik").withAge(13).build()

        javers.commit("andy", kazikV1)
        javers.commit("andy", kazikV2)

        def id = javersTestBuilder.javers().idBuilder().instanceId(new DummyUser("kazik"))

        when:
        def history = mongoRepository.getStateHistory(id, 2)

        then:
        history.size() == 2
    }
}