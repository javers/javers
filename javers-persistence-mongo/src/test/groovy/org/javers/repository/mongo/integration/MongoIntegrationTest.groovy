package org.javers.repository.mongo.integration

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.javers.core.JaversRepositoryE2ETest
import org.javers.repository.mongo.MongoRepository

import static org.javers.core.JaversBuilder.javers

/**
 * runs e2e test suite with real MongoDB at http://mongolab.com
 *
 * @author bartosz walacik
 */
class MongoIntegrationTest extends JaversRepositoryE2ETest {

    @Override
    def setup() {
        def credentials = MongoCredential.createCredential("javers", "j_int_test", "123456".toCharArray())
        def mongoDb = new MongoClient(
            new ServerAddress("ds031982.mongolab.com",31982),
            [credentials]
        ).getDatabase("j_int_test")

        def mongoRepository = new MongoRepository(mongoDb)

        mongoRepository.clean();
        javers = javers().registerJaversRepository(mongoRepository).build()
        mongoRepository.setJsonConverter(javers.getJsonConverter());
    }
}
