package org.javers.repository.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared

/**
 * @author bartosz walacik
 */
@Testcontainers
class MongoE2ETest extends JaversMongoRepositoryE2ETest {

  @Shared
  static DockerizedMongoContainer dockerizedMongoContainer = new DockerizedMongoContainer()

  @Shared
  static MongoClient mongoClient = dockerizedMongoContainer.mongoClient

  @Override
  protected MongoDatabase getMongoDb() {
    mongoClient.getDatabase("test")
  }
}
