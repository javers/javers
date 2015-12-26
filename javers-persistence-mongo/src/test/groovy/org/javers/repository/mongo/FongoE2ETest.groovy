package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import com.mongodb.client.MongoDatabase

/**
 * runs e2e test suite with Fongo
 *
 * @author bartosz walacik
 */
class FongoE2ETest extends JaversMongoRepositoryE2ETest {

  @Override
  protected MongoDatabase getMongoDb() {
    new Fongo("myDb").getDatabase("test")
  }
}
