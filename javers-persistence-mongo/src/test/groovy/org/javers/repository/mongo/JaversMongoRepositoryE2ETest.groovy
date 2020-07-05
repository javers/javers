package org.javers.repository.mongo

import com.mongodb.client.MongoDatabase
import org.javers.core.JaversRepositoryShadowE2ETest
import org.javers.core.JaversTestBuilder
import org.javers.core.commit.Commit
import org.javers.core.model.DummyUser
import org.javers.core.model.SnapshotEntity
import org.javers.repository.api.JaversRepository
import org.javers.repository.api.QueryParamsBuilder
import org.javers.repository.jql.JqlQuery
import org.javers.shadow.Shadow

import static org.javers.core.model.DummyUser.dummyUser
import static org.javers.repository.jql.QueryBuilder.byInstanceId

/**
 * runs e2e test suite with mongo db provided by subclasses
 *
 * @author bartosz walacik
 */
abstract class JaversMongoRepositoryE2ETest extends JaversRepositoryShadowE2ETest {
    protected abstract MongoDatabase getMongoDb()

    JaversTestBuilder javersTestBuilder

    @Override
    def setup() {
        repository.jsonConverter = javers.jsonConverter
        javersTestBuilder = JaversTestBuilder.javersTestAssembly()
    }

    @Override
    protected JaversRepository prepareJaversRepository() {
        MongoRepository mongoRepository = new MongoRepository(getMongoDb())
        mongoRepository.clean()
        mongoRepository
    }

    def "should commit and read snapshot of Entity containing map field with dot keys"() {
        given:
        def cdo = new SnapshotEntity(id: 1, mapOfPrimitives: ['primitive.value':1])

        when:
        javers.commit('author', cdo)
        def snapshots = javers.findSnapshots(byInstanceId(1, SnapshotEntity).build())

        then:
        snapshots[0].getPropertyValue('mapOfPrimitives') == ['primitive.value':1]
    }

    def "should persist head id"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository

        def commitFactory = javersTestBuilder.commitFactory

        def kazikV1 = dummyUser("Kazik").withAge(1)
        def kazikV2 = dummyUser("Kazik").withAge(2)

        def commit1 = commitFactory.create("author", [:], kazikV1)
        def commit2 = commitFactory.create("author", [:], kazikV2)

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
        MongoRepository mongoRepository = (MongoRepository)repository
        def commitFactory = javersTestBuilder.commitFactory

        def kazik = new DummyUser("kazik")
        def id = javersTestBuilder.instanceId(new DummyUser("kazik"))

        when:
        //persist
        mongoRepository.persist(commitFactory.create("andy", [:], kazik))

        //get last snapshot
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId == id
        latest.get().size() == 1
    }

    def "should get last commit by GlobalId"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository

        def commitFactory = javersTestBuilder.commitFactory
        def id = javersTestBuilder.globalIdFactory.createInstanceId("kazik", DummyUser)

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", [:], kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.typeName == DummyUser.name
    }

    def "should get last commit by InstanceIdDTO"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository
        def commitFactory = javersTestBuilder.commitFactory
        def id = javersTestBuilder.instanceId(new DummyUser("kazik"))

        //create entity & persist commit
        def kazik = new DummyUser("kazik")
        mongoRepository.persist(commitFactory.create("andy", [:], kazik))

        when:
        def latest = mongoRepository.getLatest(id)

        then:
        latest.get().globalId.cdoId == "kazik"
        latest.get().globalId.typeName == DummyUser.name
    }

    def "should get state history"() {
        given:
        MongoRepository mongoRepository = (MongoRepository)repository

        def kazikV1 = dummyUser("kazik").withAge(12)
        def kazikV2 = dummyUser("kazik").withAge(13)

        javers.commit("andy", kazikV1)
        javers.commit("andy", kazikV2)

        def id = javersTestBuilder.instanceId(new DummyUser("kazik"))
        def queryParams = QueryParamsBuilder.withLimit(2).build()

        when:
        def history = mongoRepository.getStateHistory(id, queryParams)

        then:
        history.size() == 2
    }

    def "CASE FOR ISSUE 958 -> should return two shadows for entity with two commits"() {
        given: "there are commits with ids 1.0 and 1.1"
            MongoRepository mongoRepository = (MongoRepository) repository

            def commitFactory = javersTestBuilder.commitFactory

            def kazikV1 = dummyUser("Kazik").withAge(1)
            def kazikV2 = dummyUser("Kazik").withAge(2)

            Commit commit1 = commitFactory.create("author", [:], kazikV1)
            Commit commit2 = commitFactory.create("author", [:], kazikV2)

            mongoRepository.persist(commit1)
            mongoRepository.persist(commit2)

        when: "query has list of commits 1.0 and 1.1"
            JqlQuery query = byInstanceId("Kazik", "org.javers.core.model.DummyUser")
                    .withCommitIds(List.of(BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.1)))
                    // ^ commenting line above makes test pass
                    .withScopeDeepPlus()
                    .limit(Integer.MAX_VALUE)
                    .build();
            List<Shadow<Object>> shadows = javers.findShadows(query);

        then: "two shadows (one per each commit) are returned"
            shadows.size() == 2
    }
}
