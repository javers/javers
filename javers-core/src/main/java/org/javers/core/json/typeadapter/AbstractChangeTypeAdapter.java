package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.json.JsonTypeAdapterTemplate;

public abstract class AbstractChangeTypeAdapter<T extends Change> extends JsonTypeAdapterTemplate<T> {

    @Override
    public T fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public JsonElement toJson(T change, JsonSerializationContext context) {
        return createJsonObject(change, context);
    }

    protected JsonObject createJsonObject(T change, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("changeType", change.getClass().getSimpleName());
        jsonObject.add("globalCdoId", context.serialize(change.getAffectedCdoId()));

        if (change instanceof PropertyChange) {
            jsonObject.addProperty("property", ((PropertyChange) change).getProperty().getName());
        }
        return jsonObject;
    }
}
