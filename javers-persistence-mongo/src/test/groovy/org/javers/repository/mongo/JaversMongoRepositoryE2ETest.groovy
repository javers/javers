package org.javers.repository.mongo

import com.mongodb.client.MongoDatabase
import org.javers.core.JaversRepositoryE2ETest
import org.javers.repository.api.JaversRepository

/**
 * runs e2e test suite with mongo db provided by subclasses
 *
 * @author bartosz walacik
 */
abstract class JaversMongoRepositoryE2ETest extends JaversRepositoryE2ETest {
    protected abstract MongoDatabase getMongoDb()

    @Override
    def setup() {
        repository.jsonConverter = javers.jsonConverter
    }

    @Override
    protected JaversRepository prepareJaversRepository() {
        MongoRepository mongoRepository = new MongoRepository(getMongoDb())
        mongoRepository.clean()
        return mongoRepository;
    }
}
