package org.javers.repository.mongo;

import org.javers.core.commit.Commit;
import org.javers.core.json.JsonConverter;
import org.javers.repository.mongo.model.MongoChange;
import org.javers.repository.mongo.model.MongoCommit;
import org.javers.repository.mongo.model.MongoSnapshot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.javers.repository.mongo.model.MongoCommit.Builder.mongoCommit;

public class ModelMapper2 {

    private final JsonConverter jsonConverter;

    public ModelMapper2(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public MongoCommit toMongoCommit(Commit commit) {
        return mongoCommit().withId(jsonConverter.toJsonElement(commit.getId()).getAsString()).build();
    }

    public MongoChange toMongoChange(Commit commit) {
        throw new NotImplementedException();
    }

    public MongoSnapshot toMongoSnaphot(Commit commit) {
        throw new NotImplementedException();
    }


}
