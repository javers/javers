package org.javers.repository.mongo

import org.javers.core.model.SnapshotEntity
import org.javers.repository.api.JaversRepository

import static org.javers.repository.mongo.MongoRepositoryConfigurationBuilder.mongoRepositoryConfiguration

class MongoE2EWithCustomCollectionNameTest extends MongoE2ETest {

    @Override
    protected JaversRepository prepareJaversRepository() {
        def mongoRepository = new MongoRepository(getMongoDb(),
                        mongoRepositoryConfiguration().withSnapshotCollectionName("jv_custom_snapshots").build())
        mongoRepository.clean()
        mongoRepository
    }

    def "get items from another mongo database collection name"() {
        when:
        javers.commit('author', new SnapshotEntity())
        def find = mongoClient.getDatabase("test")
                .getCollection("jv_custom_snapshots").find();

        then:
        find.size() == 1
        find.iterator().next().getAt("globalId_key").endsWith("SnapshotEntity/1")
    }
}
