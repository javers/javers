package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.Property;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CdoSnapshotAdapter implements JsonTypeAdapter<CdoSnapshot> {

    @Override
    public CdoSnapshot fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject commitIdAsJsonObject = json.getAsJsonObject().getAsJsonObject("commitId");
        int majorId = commitIdAsJsonObject.get("majorId").getAsInt();
        int minorId = commitIdAsJsonObject.get("minorId").getAsInt();
        CommitId commitId = new CommitId(majorId, minorId);

//        return new CdoSnapshot(commitId, new HashMap<>());
        return null;

    }

    @Override
    public JsonElement toJson(CdoSnapshot sourceValue, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        appendGlobalCdoId(sourceValue, jsonObject, context);
        appendCommitId(sourceValue, jsonObject, context);
        appendState(sourceValue, jsonObject, context);

        return jsonObject;
    }

    private void appendGlobalCdoId(CdoSnapshot cdoSnapshot, JsonObject jsonObject, JsonSerializationContext context) {
        jsonObject.add("globalId", context.serialize(cdoSnapshot.getGlobalId()));
    }

    private void appendCommitId(CdoSnapshot cdoSnapshot, JsonObject toJson, JsonSerializationContext jsonSerializationContext) {
        toJson.add("commitId", jsonSerializationContext.serialize(cdoSnapshot.getCommitId()));
    }

    private void appendState(CdoSnapshot sourceValue, JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
        jsonObject.add("state", stateAsJsonElement(sourceValue.getState(), jsonSerializationContext));

    }

    private JsonElement stateAsJsonElement(Map<Property, Object> state, JsonSerializationContext jsonSerializationContext) {
        Map<String, Object> states = new HashMap<>();

        for (Map.Entry<Property, Object> entry : state.entrySet()) {
            states.put(entry.getKey().getName(), entry.getValue().toString());
        }

        return jsonSerializationContext.serialize(states);
    }

    @Override
    public Class getValueType() {
        return CdoSnapshot.class;
    }
}
