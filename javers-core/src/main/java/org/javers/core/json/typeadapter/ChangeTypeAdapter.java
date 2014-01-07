package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.model.domain.GlobalCdoId;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class ChangeTypeAdapter implements JsonTypeAdapter<Change> {
    public static final Type[] SUPPORTED = {NewObject.class, ObjectRemoved.class, ValueChange.class, ReferenceChange.class};

    @Override
    public JsonElement toJson(Change sourceValue, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("changeType",sourceValue.getClass().getSimpleName());

        jsonObject.add("globalCdoId",globalCdoId(sourceValue.getGlobalCdoId(),context));

        if (sourceValue instanceof PropertyChange) {
            append((PropertyChange) sourceValue, jsonObject);
        }

        if (sourceValue instanceof  ValueChange) {
            append((ValueChange)sourceValue, jsonObject, context);
        }

        if (sourceValue instanceof ReferenceChange) {
            append((ReferenceChange)sourceValue, jsonObject, context);
        }

        return jsonObject;
    }

    private void append(PropertyChange change, JsonObject toJson) {
        toJson.addProperty("property",change.getProperty().getName());
    }

    private void append(ReferenceChange change, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("leftReference",  globalCdoId(change.getLeftReference(), context));
        toJson.add("rightReference", globalCdoId(change.getRightReference(), context));
    }

    private void append(ValueChange change, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("leftValue", context.serialize(change.getLeftValue().value()));
        toJson.add("rightValue", context.serialize(change.getRightValue().value()));
    }

    private JsonElement globalCdoId(GlobalCdoId globalCdoId, JsonSerializationContext context) {
        if (globalCdoId == null) {
            return JsonNull.INSTANCE;
        }

        final JsonObject jsonObject = new JsonObject();

        jsonObject.add("cdoId", context.serialize(globalCdoId.getCdoId()));
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
