package org.javers.core.json.typeadapter;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.Map;

public class CdoSnapshotTypeAdapter extends JsonTypeAdapterTemplate<CdoSnapshot> {

    private TypeMapper typeMapper;

    public CdoSnapshotTypeAdapter(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public Class getValueType() {
        return CdoSnapshot.class;
    }

    @Override
    public CdoSnapshot fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        return null;
    }

    @Override
    public JsonElement toJson(CdoSnapshot snapshot, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("commitId", snapshot.getCommitId().value());
        jsonObject.add("globalCdoId", context.serialize(snapshot.getGlobalId()));
        jsonObject.add("state", getState(snapshot, context));

        return jsonObject;
    }

    private JsonElement getState(CdoSnapshot snapshot, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<Property, Object> entry : snapshot.getState().entrySet()) {

            if (typeMapper.isPrimitiveOrValue(entry.getKey().getType())) {
                appendPrimitiveOrValue(jsonObject, entry);
            } else {// entity, value object, collection
                jsonObject.add(entry.getKey().getName(), context.serialize(entry.getValue()));
            }
        }

        return jsonObject;
    }

    private void appendPrimitiveOrValue(JsonObject jsonObject, Map.Entry<Property, Object> entry) {
        String keyName = entry.getKey().getName();
        Object value = entry.getValue();

        if (value instanceof String) {
            jsonObject.addProperty(keyName, (String) value);
        } else if (value instanceof Number) {
            jsonObject.addProperty(keyName, (Number) value);
        } else if (value instanceof Boolean) {
            jsonObject.addProperty(keyName, (Boolean) value);
        } else if (value instanceof Character) {
            jsonObject.addProperty(keyName, (Character) value);
        } else {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_FOUND);
        }
    }
}
