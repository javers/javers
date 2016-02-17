package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import org.javers.core.JaversTestBuilder
import org.javers.core.json.JsonConverter
import org.javers.core.model.DummyUser
import org.javers.repository.api.QueryParamsBuilder
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.model.DummyUser.dummyUser

/**
 * @author pawel szymczyk
 */
class MongoRepositoryFongoIntTest extends Specification {

    @Shared def mongoDb

    def setup(){
        mongoDb = new Fongo("myDb").getDatabase("test")
    }

    def "should persist head id"() {

        given:
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def mongoRepository = new MongoRepository(mongoDb, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory

        def kazikV1 = dummyUser("Kazik").withAge(1)
        def kazikV2 = dummyUser("Kazik").withAge(2)

        def commit1 = commitFactory.create("author", kazikV1)
        def commit2 = commitFactory.create("author", kazikV2)

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
        def mongoRepository = new MongoRepository(mongoDb, javersTestBuilder.jsonConverter)
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
        def mongoRepository = new MongoRepository(mongoDb, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory
        def id = javersTestBuilder.globalIdFactory.createInstanceId("kazik", DummyUser)

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.typeName == DummyUser.name
    }

    def "should get last commit by InstanceIdDTO"() {

        given:
        //create components
        def javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        def mongoRepository = new MongoRepository(mongoDb, javersTestBuilder.jsonConverter)
        def commitFactory = javersTestBuilder.commitFactory
        def id = javersTestBuilder.javers().idBuilder().instanceId(new DummyUser("kazik"))

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.typeName == DummyUser.name
    }

    def "should get state history"() {

        given:
        def mongoRepository = new MongoRepository(mongoDb, Stub(JsonConverter))

        def javersTestBuilder = JaversTestBuilder.javersTestAssembly(mongoRepository)

        mongoRepository.setJsonConverter(javersTestBuilder.jsonConverter)

        def javers = javersTestBuilder.javers()

        def kazikV1 = dummyUser("kazik").withAge(12)
        def kazikV2 = dummyUser("kazik").withAge(13)

        javers.commit("andy", kazikV1)
        javers.commit("andy", kazikV2)

        def id = javersTestBuilder.javers().idBuilder().instanceId(new DummyUser("kazik"))
        def queryParams = QueryParamsBuilder.withLimit(2).build()

        when:
        def history = mongoRepository.getStateHistory(id, queryParams)

        then:
        history.size() == 2
    }
}