package org.javers.repository.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared

import static org.javers.core.model.DummyUser.dummyUser

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

  def "should persist head id"() {
    given:
    MongoRepository mongoRepository = (MongoRepository)repository

    def commitFactory = javersTestBuilder.commitFactory

    def kazikV1 = dummyUser("Kazik").withAge(1)
    def kazikV2 = dummyUser("Kazik").withAge(2)

    def commit1 = commitFactory.create("author", [:], kazikV1)
    def commit2 = commitFactory.create("author", [:], kazikV2)

    when:
    mongoRepository.persist(commit1)

    then:
    mongoRepository.getHeadId().getMajorId() == 1
    mongoRepository.getHeadId().getMinorId() == 0

    when:
    mongoRepository.persist(commit2)

    then:
    mongoRepository.getHeadId().getMajorId() == 1
    mongoRepository.getHeadId().getMinorId() == 1
  }
}
