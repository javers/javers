package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.TypeMapper;

import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;

/**
 * @author pawel szymczyk
 */
class CdoSnapshotTypeAdapter extends JsonTypeAdapterTemplate<CdoSnapshot> {

    public static final String GLOBAL_CDO_ID = "globalId";
    public static final String COMMIT_METADATA = "commitMetadata";
    public static final String STATE_NAME = "state";
    public static final String INITIAL_NAME_LEGACY = "initial";
    public static final String TYPE_NAME = "type";

    private TypeMapper typeMapper;

    public CdoSnapshotTypeAdapter(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public Class getValueType() {
        return CdoSnapshot.class;
    }

    @Override
    public CdoSnapshot fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;

        CommitMetadata commitMetadata = context.deserialize(((JsonObject) json).get(COMMIT_METADATA), CommitMetadata.class);

        GlobalId cdoId = context.deserialize(jsonObject.get(GLOBAL_CDO_ID), GlobalId.class);

        CdoSnapshotBuilder cdoSnapshotBuilder = cdoSnapshot(cdoId, commitMetadata);

        deserializeType(jsonObject,cdoSnapshotBuilder);

        JsonElement state = jsonObject.get(STATE_NAME);
        CdoSnapshotStateDeserializer stateDeserializer = new CdoSnapshotStateDeserializer(typeMapper, context);
        CdoSnapshotState snapshotState = stateDeserializer.deserialize(state, cdoId);

        return cdoSnapshotBuilder.withState(snapshotState).build();
    }

    private void deserializeType(JsonObject jsonObject, CdoSnapshotBuilder cdoSnapshotBuilder){
        JsonElement initial = jsonObject.get(INITIAL_NAME_LEGACY);
        if (initial != null){ //for legacy JSON's
            cdoSnapshotBuilder.withInitial(initial.getAsBoolean());
            return;
        }

        JsonElement type = jsonObject.get(TYPE_NAME);
        if (type != null) {
            cdoSnapshotBuilder.withType(SnapshotType.valueOf(type.getAsString()));
        }
    }

    @Override
    public JsonElement toJson(CdoSnapshot snapshot, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.add(COMMIT_METADATA, context.serialize(snapshot.getCommitMetadata()));
        jsonObject.add(GLOBAL_CDO_ID, context.serialize(snapshot.getGlobalId()));
        jsonObject.add(STATE_NAME, context.serialize(snapshot.getState()));
        jsonObject.add(TYPE_NAME, context.serialize(snapshot.getType().name()));

        return jsonObject;
    }
}
