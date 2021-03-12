package org.javers.core.examples;

import org.bson.types.ObjectId;
import org.fest.assertions.api.Assertions;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.cases.MongoStoredEntity;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.junit.Test;

public class JsonTypeAdapterExample {

    @Test
    public void shouldSerializeValueToJsonWithTypeAdapter() {
        //given
        Javers javers = JaversBuilder.javers()
                .registerValueTypeAdapter(new ObjectIdTypeAdapter())
                .build();

        //when
        ObjectId id = ObjectId.get();
        MongoStoredEntity entity = new MongoStoredEntity(id, "alg1", "1.0", "name");
        javers.commit("author", entity);
        CdoSnapshot snapshot = javers.getLatestSnapshot(id, MongoStoredEntity.class).get();

        //then
        String json = javers.getJsonConverter().toJson(snapshot);
        Assertions.assertThat(json).contains(id.toString());

        System.out.println(json);
    }
}
