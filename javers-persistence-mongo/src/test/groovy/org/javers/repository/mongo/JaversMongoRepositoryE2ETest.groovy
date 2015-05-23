package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import org.javers.core.JaversRepositoryE2ETest

import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz walacik
 */
class JaversMongoRepositoryE2ETest extends JaversRepositoryE2ETest {

    @Override
    def setup() {
        def mongoDb = new Fongo("myDb").getDatabase("test")
        // def mongoDb = new MongoClient( "localhost" ).getDatabase("test")

        def mongoRepository = new MongoRepository(mongoDb)

        mongoRepository.clean();
        javers = javers().registerJaversRepository(mongoRepository).build()
        mongoRepository.setJsonConverter(javers.getJsonConverter());
    }
}
