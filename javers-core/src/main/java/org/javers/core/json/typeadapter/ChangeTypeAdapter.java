package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.InstanceId;
import org.javers.model.domain.ValueObjectId;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class ChangeTypeAdapter implements JsonTypeAdapter<Change> {
    public static final Type[] SUPPORTED = {NewObject.class, ObjectRemoved.class, ValueChange.class, ReferenceChange.class};

    @Override
    public JsonElement toJson(Change change, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();


        appendChangeType(change, jsonObject);
        appendGlobalId(change.getGlobalCdoId(), jsonObject, context);

        if (change instanceof PropertyChange) {
            appendPropertyName((PropertyChange) change, jsonObject);
        }

        if (change instanceof  ValueChange) {
            appendBody((ValueChange) change, jsonObject, context);
        }

        if (change instanceof ReferenceChange) {
            appendBody((ReferenceChange) change, jsonObject, context);
        }

        return jsonObject;
    }

    private void appendChangeType(Change change, JsonObject toJson) {
        toJson.addProperty("changeType", change.getClass().getSimpleName());
    }

    private void appendPropertyName(PropertyChange change, JsonObject toJson) {
        toJson.addProperty("property",change.getProperty().getName());
    }

    private void appendBody(ReferenceChange change, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("leftReference",  globalCdoId(change.getLeftReference(), context));
        toJson.add("rightReference", globalCdoId(change.getRightReference(), context));
    }

    private void appendBody(ValueChange change, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("leftValue", context.serialize(change.getLeftValue().value()));
        toJson.add("rightValue", context.serialize(change.getRightValue().value()));
    }

    private void appendGlobalId(GlobalCdoId globalCdoId, JsonObject toJson, JsonSerializationContext context) {
        if(globalCdoId instanceof InstanceId) {
            toJson.add("instanceId", globalCdoId(globalCdoId, context));
        }
        if(globalCdoId instanceof ValueObjectId) {
            toJson.add("valueObjectId", valueObjectId((ValueObjectId) globalCdoId, context));
        }
    }

    private JsonElement globalCdoId(GlobalCdoId globalCdoId, JsonSerializationContext context) {
        if (globalCdoId == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject jsonObject = new JsonObject();

        jsonObject.add("cdoId", context.serialize(globalCdoId.getCdoId()));
        jsonObject.addProperty("entity", globalCdoId.getEntity().getSourceClass().getName());

        return jsonObject;
    }

    private JsonElement valueObjectId(ValueObjectId valueObjectId, JsonSerializationContext context) {
        if (valueObjectId == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject jsonObject = (JsonObject) globalCdoId(valueObjectId, context);
        jsonObject.addProperty("fragment", valueObjectId.getFragment());

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
