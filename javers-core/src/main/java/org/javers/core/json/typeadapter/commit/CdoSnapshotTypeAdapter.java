package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.DuckType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.*;

import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;

/**
 * @author pawel szymczyk
 */
class CdoSnapshotTypeAdapter extends JsonTypeAdapterTemplate<CdoSnapshot> {

    static final String GLOBAL_CDO_ID = "globalId";
    static final String COMMIT_METADATA = "commitMetadata";
    static final String STATE_NAME = "state";
    static final String INITIAL_NAME_LEGACY = "initial";
    static final String TYPE_NAME = "type";
    static final String CHANGED_NAME = "changedProperties";
    static final String VERSION = "version";

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
        JsonObject stateObject = (JsonObject)jsonObject.get(STATE_NAME);

        GlobalId cdoId = context.deserialize(jsonObject.get(GLOBAL_CDO_ID), GlobalId.class);
        Long version = context.deserialize(jsonObject.get(VERSION), Long.class);
        DuckType duckType = new DuckType(cdoId.getTypeName(), extractPropertyNames(stateObject));

        ManagedType managedType = typeMapper.getJaversManagedType(duckType, ManagedType.class);

        CdoSnapshotBuilder builder = cdoSnapshot(cdoId, managedType);
        deserializeType(jsonObject, builder);

        CdoSnapshotState snapshotState = deserializeSnapshotState(context, stateObject, managedType);
        CommitMetadata commitMetadata = context.deserialize(((JsonObject) json).get(COMMIT_METADATA), CommitMetadata.class);
        List<String> changedProperties = deserializeChangedProperties(jsonObject, context);

        return builder
                .withState(snapshotState)
                .withVersion(version)
                .withCommitMetadata(commitMetadata)
                .withChangedProperties(changedProperties)
                .build();
    }

    private CdoSnapshotState deserializeSnapshotState(JsonDeserializationContext context, JsonObject stateObject, ManagedType managedType) {
        CdoSnapshotStateDeserializer stateDeserializer = new CdoSnapshotStateDeserializer(typeMapper, context);
        return stateDeserializer.deserialize(stateObject, managedType);
    }

    private Set<String> extractPropertyNames(JsonObject state){
        Set<String> propertyNames = new HashSet<>();
        for(Map.Entry<String, JsonElement> entry : state.entrySet()){
            propertyNames.add(entry.getKey());
        }
        return propertyNames;
    }

    private List<String> deserializeChangedProperties(JsonObject jsonObject, JsonDeserializationContext context){
        JsonElement propsElement = jsonObject.get(CHANGED_NAME);
        if (propsElement == null){ //for legacy JSON's
            return Collections.emptyList();
        }
        return context.deserialize(propsElement, List.class);
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
        jsonObject.add(CHANGED_NAME, context.serialize(snapshot.getChanged()));
        jsonObject.add(TYPE_NAME, context.serialize(snapshot.getType().name()));
        jsonObject.add(VERSION, context.serialize(snapshot.getVersion()));

        return jsonObject;
    }
}
