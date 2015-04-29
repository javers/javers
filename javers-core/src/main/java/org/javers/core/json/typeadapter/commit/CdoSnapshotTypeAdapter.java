package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

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
    public static final String CHANGED_NAME = "changedProperties";

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

        deserializeType(jsonObject, cdoSnapshotBuilder);
        deserializeChangedProperties(jsonObject, cdoSnapshotBuilder, context);

        JsonElement state = jsonObject.get(STATE_NAME);
        CdoSnapshotStateDeserializer stateDeserializer = new CdoSnapshotStateDeserializer(typeMapper, context);
        CdoSnapshotState snapshotState = stateDeserializer.deserialize(state, cdoId);

        return cdoSnapshotBuilder.withState(snapshotState).build();
    }

    private void deserializeChangedProperties(JsonObject jsonObject, CdoSnapshotBuilder builder, JsonDeserializationContext context){
        JsonElement propsElement = jsonObject.get(CHANGED_NAME);
        if (propsElement == null){ //for legacy JSON's
            return;
        }
        List<String> changedPropNames = context.deserialize(propsElement, List.class);
        builder.withChangedProperties(changedPropNames);
    }

    private void deserializeType(JsonObject jsonObject, CdoSnapshotBuilder builder){
        JsonElement initial = jsonObject.get(INITIAL_NAME_LEGACY);
        if (initial != null){ //for legacy JSON's
            builder.withInitial(initial.getAsBoolean());
            return;
        }

        JsonElement type = jsonObject.get(TYPE_NAME);
        if (type != null) {
            builder.withType(SnapshotType.valueOf(type.getAsString()));
        }
    }

    @Override
    public JsonElement toJson(CdoSnapshot snapshot, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.add(COMMIT_METADATA, context.serialize(snapshot.getCommitMetadata()));
        jsonObject.add(GLOBAL_CDO_ID, context.serialize(snapshot.getGlobalId()));
        jsonObject.add(STATE_NAME, context.serialize(snapshot.getState()));
        jsonObject.add(CHANGED_NAME, context.serialize(snapshot.getChangedPropertyNames()));
        jsonObject.add(TYPE_NAME, context.serialize(snapshot.getType().name()));

        return jsonObject;
    }
}
