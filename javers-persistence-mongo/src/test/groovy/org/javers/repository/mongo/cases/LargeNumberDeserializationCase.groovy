package org.javers.repository.mongo.cases

import com.mongodb.client.MongoDatabase
import jakarta.persistence.Id
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.repository.mongo.MongoRepository
import org.javers.repository.mongo.BaseMongoTest
import spock.lang.Shared


class LargeNumberDeserializationCase extends BaseMongoTest {
  public static final long ID_ONE_BILLION = 1000000000L
  public static final long ID_ONE_TRILLION = 1000000000L * 1000

  @Shared
  Javers javers

  def setupSpec() {
    MongoDatabase mongo = mongoClient.getDatabase("test")

    MongoRepository mongoRepo = new MongoRepository(mongo)
    javers = JaversBuilder.javers().registerJaversRepository(mongoRepo).build()
  }

  static class MyEntity {
    @Id
    final private Long id
    final private String name

    MyEntity(Long id, String name) {
      this.id = id
      this.name = name
    }
  }

  def verifyMappingOfLargeId() {
    when:
    javers.commit("kent", new MyEntity(ID_ONE_BILLION, "red"))
    javers.commit("kent", new MyEntity(ID_ONE_BILLION, "blue"))

    then:
    noExceptionThrown()
  }

  def verifyMappingOfLargerId() {
    when:
    javers.commit("kent", new MyEntity(ID_ONE_TRILLION, "red"))
    javers.commit("kent", new MyEntity(ID_ONE_TRILLION, "blue"))

    then:
    noExceptionThrown()
  }
}
