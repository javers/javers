package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.DummyUser
import org.javers.test.builder.DummyUserBuilder
import spock.lang.Specification

/**
 * @author pawel szymczyk
 */
class MongoRepositoryIntTest extends Specification {

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

    def "should get state history"() {

        given:
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory

        def kazikV1 = DummyUserBuilder.dummyUser("kazik").withAge(12).build()
        def kazikV2 = DummyUserBuilder.dummyUser("kazik").withAge(13).build()

        mongoRepository.persist(commitFactory.create("andy", kazikV1))
        mongoRepository.persist(commitFactory.create("andy", kazikV2))

        def id = InstanceId.InstanceIdDTO.instanceId("kazik", DummyUser)

        when:
        def history = mongoRepository.getStateHistory(id, 2)

        then:
        history
    }
}