package org.javers.repository.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.javers.core.model.SnapshotEntity
import spock.lang.Shared

class MongoE2EWithRandomGeneratorTest extends JaversMongoRepositoryE2ETest {

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }

    @Shared
    static DockerizedMongoContainer dockerizedMongoContainer = new DockerizedMongoContainer()

    @Shared
    static MongoClient mongoClient = dockerizedMongoContainer.mongoClient

    @Override
    protected MongoDatabase getMongoDb() {
        mongoClient.getDatabase("test")
    }

    def "should not persist HeadId when RANDOM CommitId generator is configured" () {
        given:
        def anEntity = new SnapshotEntity(id: 1, intProperty: 100)

        when:
        def commit = javers.commit("author", anEntity)

        then:
        !repository.getHeadId()
    }
}
