package org.javers.repository.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;

import javax.persistence.Id;
import java.net.UnknownHostException;

public class Test {

    public static void main(String[] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient("localhost" , 27017);
        DB db = mongoClient.getDB("myDb");

        MongoRepository mongoRepository = new MongoRepository(db);

        Javers javers = JaversBuilder.javers()
                .registerJaversRepository(mongoRepository)
                .build();

        String author = "Pawel";

        MyEntity myEntity = new MyEntity(1, "Some test value");

        //initial commit
        javers.commit(author, myEntity);

        //change something and commit again
        myEntity.setValue("Another test value");
        javers.commit(author, myEntity);
    }

    private static class MyEntity {

        @Id
        private int id;
        private String value;

        private MyEntity(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

Afr
}
