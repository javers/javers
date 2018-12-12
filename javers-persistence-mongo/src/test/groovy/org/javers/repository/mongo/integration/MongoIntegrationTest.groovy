package org.javers.repository.mongo.integration

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import org.javers.repository.mongo.JaversMongoRepositoryE2ETest
import spock.lang.Ignore

/**
 * runs e2e test suite with real MongoDB at localhost
 *
 * @author bartosz walacik
 */
@Ignore //it fails on Travis-CI since 2018-12, we don't know why
class MongoIntegrationTest extends JaversMongoRepositoryE2ETest {

    @Override
    protected MongoDatabase getMongoDb() {
        new MongoClient().getDatabase("j_int_test")
    }
}
