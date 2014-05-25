package org.javers.repository.mongo;

import com.google.gson.JsonObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.repository.mongo.model.MongoCdoSnapshots;
import org.javers.repository.mongo.model.MongoSnapshot;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ModelMapper {

    private final JsonConverter jsonConverter;

    public ModelMapper(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public CdoSnapshot toCdoSnapshot(MongoSnapshot latest, String globalCdoId) {
        JsonObject jsonObject = (JsonObject) jsonConverter.toJsonElement(latest);

        //TODO refactor
        jsonObject.add("globalCdoId", jsonConverter.toJsonElement(jsonConverter.fromJson(globalCdoId, GlobalCdoId.class)));
        return jsonConverter.fromJson(jsonObject.toString(), CdoSnapshot.class);
    }

    public MongoCdoSnapshots toMongoCdoSnapshot(CdoSnapshot snapshot) {
        DBObject globalCdoId = (DBObject) JSON.parse(jsonConverter.toJson(snapshot.getGlobalId()));
        List<MongoSnapshot> snapshots = toSnapshots(Lists.immutableListOf(snapshot));

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
}
