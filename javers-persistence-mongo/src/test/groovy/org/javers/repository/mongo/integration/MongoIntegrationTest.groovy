package org.javers.repository.mongo.integration

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import org.javers.repository.mongo.JaversMongoRepositoryE2ETest

/**
 * runs e2e test suite with real MongoDB at localhost
 *
 * @author bartosz walacik
 */
class MongoIntegrationTest extends JaversMongoRepositoryE2ETest {

    @Override
    protected MongoDatabase getMongoDb() {
        new MongoClient().getDatabase("j_int_test")
    }
}
