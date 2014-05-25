package org.javers.repository.mongo;

import com.google.gson.JsonObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.core.commit.Commit;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.repository.mongo.model.MongoCdoSnapshots;
import org.javers.repository.mongo.model.MongoChange;
import org.javers.repository.mongo.model.MongoCommit;
import org.javers.repository.mongo.model.MongoSnapshot;

import java.util.List;

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
        return null;
    }

    public MongoCdoSnapshots toMongoSnaphot(Commit commit) {
        DBObject globalCdoId = (DBObject) JSON.parse(jsonConverter.toJson(commit.getGlobalCdoId()));
        List<MongoSnapshot> snapshots = toSnapshots(commit.getSnapshots());

        return new MongoCdoSnapshots(globalCdoId, snapshots);
    }

    private List<MongoSnapshot> toSnapshots(final List<CdoSnapshot> snapshots) {
        return Lists.transform(snapshots, new Function<CdoSnapshot, MongoSnapshot>() {
            @Override
            public MongoSnapshot apply(CdoSnapshot snapshot) {
                return new MongoSnapshot(
                        jsonConverter.toJsonElement(snapshot.getCommitId()).getAsString(),
                        (DBObject) JSON.parse(((JsonObject) jsonConverter.toJsonElement(snapshot)).get("state").toString())
                );
            }
        });
    }


    public CdoSnapshot toCdoSnapshot(MongoSnapshot latest, String globalCdoId) {
        JsonObject jsonObject = (JsonObject) jsonConverter.toJsonElement(latest);

        //TODO refactor
        jsonObject.add("globalCdoId", jsonConverter.toJsonElement(jsonConverter.fromJson(globalCdoId, GlobalCdoId.class)));
        return jsonConverter.fromJson(jsonObject.toString(), CdoSnapshot.class);
    }
}
