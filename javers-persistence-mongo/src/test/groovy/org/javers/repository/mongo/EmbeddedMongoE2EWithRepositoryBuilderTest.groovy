package org.javers.repository.mongo

import org.javers.repository.api.JaversRepository

class EmbeddedMongoE2EWithRepositoryBuilderTest extends EmbeddedMongoE2ETest {

    @Override
    protected JaversRepository prepareJaversRepository() {
        MongoRepository mongoRepository = MongoRepositoryBuilder.mongoRepository()
                .withMongoDatabase(getMongoDb())
                .withSnapshotCollectionName("jv_custom_snapshots")
                .build();
        mongoRepository.clean()
        mongoRepository
    }

    def "get items from another mongo database collection name"() {
        given:
        def cdo = new HashMap<String, String>();
        cdo.put("name", "updatedName");

        when:
        javers.commit('author', cdo)
        def find = mongoClient.getDatabase("test").getCollection("jv_custom_snapshots").find();

        then:
        find.size() > 0
    }
}
