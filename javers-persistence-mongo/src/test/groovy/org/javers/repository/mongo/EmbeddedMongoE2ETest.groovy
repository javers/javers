package org.javers.repository.mongo


import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase

import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory
import spock.lang.Shared

/**
 * @author bartosz walacik
 */
class EmbeddedMongoE2ETest extends JaversMongoRepositoryE2ETest {

  @Shared MongoClient mongoClient = MongodForTestsFactory.with(Version.Main.PRODUCTION).newMongo()

  @Override
  protected MongoDatabase getMongoDb() {
    mongoClient.getDatabase("test")
  }
}
