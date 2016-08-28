package org.javers.repository.mongo

import com.mongodb.client.MongoDatabase
import org.javers.core.JaversRepositoryE2ETest
import org.javers.core.model.SnapshotEntity
import org.javers.core.snapshot.SnapshotsAssert
import org.javers.repository.api.JaversRepository

import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.repository.jql.QueryBuilder.byInstanceId

/**
 * runs e2e test suite with mongo db provided by subclasses
 *
 * @author bartosz walacik
 */
abstract class JaversMongoRepositoryE2ETest extends JaversRepositoryE2ETest {
    protected abstract MongoDatabase getMongoDb()

    @Override
    def setup() {
        repository.jsonConverter = javers.jsonConverter
    }

    @Override
    protected JaversRepository prepareJaversRepository() {
        MongoRepository mongoRepository = new MongoRepository(getMongoDb())
        mongoRepository.clean()
        return mongoRepository;
    }

    def "should commit and read snapshot of Entity containing map field with dot keys"() {
        given:
        def cdo = new SnapshotEntity(id: 1,
                                     mapOfPrimitives: ["primitive.value":1])

        when:
        javers.commit("author", cdo)
        def snapshots = javers.findSnapshots(byInstanceId(1, SnapshotEntity).build())

        then:
        def cdoId = instanceId(1,SnapshotEntity)
        SnapshotsAssert.assertThat(snapshots)
                .hasSnapshot(cdoId, "1.0", [id:1,
                                            mapOfPrimitives: ["primitive.value":1]])
    }
}
