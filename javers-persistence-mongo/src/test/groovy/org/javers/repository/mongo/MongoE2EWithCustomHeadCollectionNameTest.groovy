package org.javers.repository.mongo

import org.javers.core.model.SnapshotEntity
import org.javers.repository.api.JaversRepository

import static org.javers.repository.mongo.MongoRepositoryConfigurationBuilder.mongoRepositoryConfiguration

class MongoE2EWithCustomHeadCollectionNameTest extends MongoE2ETest {

    @Override
    protected JaversRepository prepareJaversRepository() {
        def mongoRepository = new MongoRepository(getMongoDb(),
                        mongoRepositoryConfiguration()
                                .withSnapshotCollectionName("jv_custom_snapshots")
                                .withHeadCollectionName("jv_custom_head_id")
                                .build())
        mongoRepository.clean()
        mongoRepository
    }

    def "get commit head id from another mongo database collection name"() {
        when:
        javers.commit('author', new SnapshotEntity())
        def find = mongoClient.getDatabase("test")
                .getCollection("jv_custom_head_id").find();

        then:
        find.size() == 1
        find.iterator().next().getAt("id") != null
    }
}
