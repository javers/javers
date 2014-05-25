package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import org.javers.core.Javers
import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.DummyUser
import org.javers.test.builder.DummyUserBuilder
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author pawel szymczyk
 */
class MongoRepositoryIntTest extends Specification {

    def "should get headId"() {

        given:
        def mongoRepository = new MongoRepository(new Fongo("myDb").mongo.getDB("test"))
        def javers = getJaversTestInstance(mongoRepository)
        def kazik = new DummyUser("kazik")

        when:
        //first commit
        javers.commit("author", kazik)

        def headId = mongoRepository.getHeadId()

        then:
        headId.getMajorId() == 1
        headId.getMinorId() == 0

        when:
        //change something
        kazik.sex = DummyUser.Sex.FEMALE

        //next commit
        javers.commit("author", kazik)

        headId = mongoRepository.getHeadId()

        then:
        headId.getMajorId() == 2
        headId.getMinorId() == 0
    }

    def "should persist commit"() {

        given:
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory

        def kazik = new DummyUser("kazik")
        def id = InstanceId.InstanceIdDTO.instanceId("kazik", DummyUser)

        when:
        mongoRepository.persist(commitFactory.create("andy", kazik))

        then:
        def latest = mongoRepository.getLatest(id)
        latest.get().globalId == id
        latest.get().size() == 1
    }

    def "should get last commit by GlobalCdoId"() {

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
        latest.get().globalId.cdoClass.sourceClass == DummyUser
    }

    def "should get last commit by InstanceIdDTO"() {

        given:
        //create components
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory
        def id = InstanceId.InstanceIdDTO.instanceId("kazik", DummyUser)

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.cdoClass.sourceClass == DummyUser
    }

    @Ignore
    def "should get state history"() {

        given:
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db)

        def javersTestBuilder = JaversTestBuilder.javersTestAssembly(mongoRepository)

        mongoRepository.setJsonConverter(javersTestBuilder.jsonConverter)

        def javers = javersTestBuilder.javers()

        def kazikV1 = DummyUserBuilder.dummyUser("kazik").withAge(12).build()
        def kazikV2 = DummyUserBuilder.dummyUser("kazik").withAge(13).build()

        javers.commit("andy", kazikV1)
        javers.commit("andy", kazikV2)

        def id = InstanceId.InstanceIdDTO.instanceId("kazik", DummyUser)

        when:
        def history = mongoRepository.getStateHistory(id, 2)

        then:
        history
    }

    Javers getJaversTestInstance(MongoRepository mongoRepository) {
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly(mongoRepository)
        mongoRepository.setJsonConverter(javersTestBuilder.jsonConverter)
        javersTestBuilder.javers()
    }
}