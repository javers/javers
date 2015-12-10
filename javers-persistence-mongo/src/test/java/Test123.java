import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.mongo.MongoRepository;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Id;

public class Test123 {
    public static final long ID_ONE_BILLION = 1000000000L;
    public static final long ID_ONE_TRILLION = 1000000000L * 1000;

    private Javers javers;

    MongoDatabase mongoDb;

    @Before
    public void setup() {
        mongoDb = new MongoClient("localhost").getDatabase("test");

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

    @Test
    public void should() throws Throwable {
        MyEntity e = new MyEntity(ID_ONE_TRILLION, "red");
        Gson gson = new Gson();
        Document d = Document.parse(gson.toJson(e));
        mongoDb.getCollection("test").insertOne(d);

        Document found = mongoDb.getCollection("jv_snapshots").find().first();
        CdoSnapshot gsonDoc = gson.fromJson(found.toJson(), CdoSnapshot.class);
    }
}
