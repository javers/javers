package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import com.mongodb.MongoClient
import org.javers.core.JaversRepositoryE2ETest

import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz walacik
 */
class JaversMongoRepositoryE2ETest extends JaversRepositoryE2ETest {

    @Override
    def setup() {
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db)

        // def mongoClient = new MongoClient()
        // def mongoRepository =  new MongoRepository(mongoClient.getDB("test"))

        mongoRepository.clean();
        javers = javers().registerJaversRepository(mongoRepository).build()
        mongoRepository.setJsonConverter(javers.getJsonConverter());
    }
}
