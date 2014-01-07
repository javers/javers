package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.model.domain.GlobalCdoId;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class ChangeTypeAdapter implements JsonTypeAdapter<Change> {
    public static final Type[] SUPPORTED = {NewObject.class, ObjectRemoved.class, ValueChange.class};

    @Override
    public JsonElement toJson(Change sourceValue, JsonSerializationContext jsonSerializationContext) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("changeType",sourceValue.getClass().getSimpleName());

        jsonObject.add("globalCdoId",globalCdoId(sourceValue.getGlobalCdoId(),jsonSerializationContext));

        if (sourceValue instanceof PropertyChange) {
            append((PropertyChange) sourceValue, jsonObject);
        }

        if (sourceValue instanceof  ValueChange) {
           append((ValueChange)sourceValue, jsonObject,jsonSerializationContext);
        }

        return jsonObject;
    }

    private void append(PropertyChange change, JsonObject toJson) {
        toJson.addProperty("property",change.getProperty().getName());
    }

    private void append(ValueChange change, JsonObject toJson, JsonSerializationContext jsonSerializationContext) {
        toJson.add("leftValue", jsonSerializationContext.serialize(change.getLeftValue().value()));
        toJson.add("rightValue", jsonSerializationContext.serialize(change.getRightValue().value()));
    }

    private JsonElement globalCdoId(GlobalCdoId globalCdoId, JsonSerializationContext jsonSerializationContext) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.add("cdoId", jsonSerializationContext.serialize(globalCdoId.getLocalCdoId()));

        jsonObject.addProperty("entity", globalCdoId.getEntity().getSourceClass().getName());

        return jsonObject;
    }

    @Override
    public Change fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
