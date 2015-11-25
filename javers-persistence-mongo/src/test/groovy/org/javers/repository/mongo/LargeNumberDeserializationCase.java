package org.javers.repository.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Id;

public class LargeNumberDeserializationCase {
  public static final long ID_ONE_BILLION = 1000000000L;
  public static final long ID_ONE_TRILLION = 1000000000L * 1000;

  private Javers javers;

  @Before
  public void setup() {
    MongoDatabase mongoDb = new MongoClient("localhost").getDatabase("test");

    MongoRepository mongoRepo = new MongoRepository(mongoDb);
    javers = JaversBuilder.javers().registerJaversRepository(mongoRepo).build();
  }

  public static class MyEntity {
    @Id
    final private Long id;
    final private String name;

    public MyEntity(Long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  @Test
  // This test passes
  public void verifyMappingOfLargeId() {
    javers.commit("kent", new MyEntity(ID_ONE_BILLION, "red"));
    javers.commit("kent", new MyEntity(ID_ONE_BILLION, "blue"));
  }

  @Test
  // This test fails
  public void verifyMappingOfLargerId() {
    javers.commit("kent", new MyEntity(ID_ONE_TRILLION, "red"));
    javers.commit("kent", new MyEntity(ID_ONE_TRILLION, "blue"));
  }
}
