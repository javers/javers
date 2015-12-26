package org.javers.repository.mongo

import com.mongodb.client.MongoDatabase
import org.javers.core.JaversBuilder
import org.javers.core.JaversRepositoryE2ETest

/**
 * runs e2e test suite with mongo db provided by subclasses
 *
 * @author bartosz walacik
 */
abstract class JaversMongoRepositoryE2ETest extends JaversRepositoryE2ETest {
    protected MongoRepository mongoRepository

    protected abstract MongoDatabase getMongoDb()

    @Override
    def setup() {
        mongoRepository.jsonConverter = javers.jsonConverter
    }

    @Override
    JaversBuilder configureJavers(JaversBuilder javersBuilder) {
        super.configureJavers(javersBuilder)
        initializeMongoRepository()
        javersBuilder.registerJaversRepository(mongoRepository)
    }

    protected void initializeMongoRepository() {
        MongoDatabase mongoDb = getMongoDb()
        mongoRepository = new MongoRepository(mongoDb)
        mongoRepository.clean()
    }
}
