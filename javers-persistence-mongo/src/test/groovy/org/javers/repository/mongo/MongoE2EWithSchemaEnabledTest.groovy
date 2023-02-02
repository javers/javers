package org.javers.repository.mongo


import org.javers.repository.api.JaversRepository

import static org.javers.repository.mongo.MongoRepositoryConfigurationBuilder.mongoRepositoryConfiguration

class MongoE2EWithSchemaEnabledTest extends MongoE2ETest {

    @Override
    protected JaversRepository prepareJaversRepository() {
        def mongoRepository = new MongoRepository(getMongoDb(),
                mongoRepositoryConfiguration()
                        .withSnapshotCollectionName("jv_custom_snapshots_")
                        .withHeadCollectionName("jv_custom_head_id_")
                        .withSchemaManagementEnabled(false)
                        .build())
        mongoRepository.clean()
        mongoRepository
    }

    def "should not create custom indexes when schemaManagementEnabled set false"() {
        when:
        var indexListSnapshotCollection = mongoDb
                .getCollection("jv_custom_snapshots_")
                .listIndexes().toList()
        var indexListHeadCollection = mongoDb
                .getCollection("jv_custom_head_id_")
                .listIndexes().toList()

        then:
        indexListSnapshotCollection.size() == 1
        indexListSnapshotCollection.get(0).get("name") == "_id_"

        indexListHeadCollection.size() == 1
        indexListHeadCollection.get(0).get("name") == "_id_"

    }
}
