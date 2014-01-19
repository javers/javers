package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.ValueObjectId;
import org.javers.model.mapping.Entity;

import java.lang.reflect.Type;

/**
 * Change to JSON serializer
 *
 * @author bartosz walacik
 */
public class ChangeTypeAdapter implements JsonTypeAdapter<Change> {
    public static final Type[] SUPPORTED = {
            NewObject.class, ObjectRemoved.class, ValueChange.class, ReferenceChange.class, EntryAdded.class, EntryRemoved.class, EntryChanged.class};

    @Override
    public JsonElement toJson(Change change, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();


        appendChangeType(change, jsonObject);
        appendGlobalId(change.getGlobalCdoId(), jsonObject, context);

        if (change instanceof PropertyChange) {
            appendPropertyName((PropertyChange) change, jsonObject);
        }

        //lame double dispatch
        if (change instanceof  ValueChange) {
            appendBody((ValueChange) change, jsonObject, context);
        }

        if (change instanceof ReferenceChange) {
            appendBody((ReferenceChange) change, jsonObject, context);
        }

        if (change instanceof EntryChanged) {
            appendBody((EntryChanged) change, jsonObject, context);
        }

        if (change instanceof EntryAdded) {
            appendBody((EntryAdded) change, jsonObject, context);
        }

        if (change instanceof EntryRemoved) {
            appendBody((EntryRemoved) change, jsonObject, context);
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

    private void appendBody(EntryAdded change, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("entry", context.serialize(change.getAdded()));
    }

    private void appendBody(EntryRemoved change, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("entry", context.serialize(change.getEntry()));
    }

    private void appendBody(EntryChanged change, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("key", context.serialize(change.getKey()));
        toJson.add("leftValue", context.serialize(change.getLeftValue()));
        toJson.add("rightValue", context.serialize(change.getRightValue()));
    }

    private void appendGlobalId(GlobalCdoId globalCdoId, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("globalCdoId", globalCdoId(globalCdoId, context));
    }

    private JsonElement globalCdoId(GlobalCdoId globalCdoId, JsonSerializationContext context) {
        if (globalCdoId == null) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();

        //managedClass
        if (globalCdoId.getCdoClass() instanceof Entity) {
            jsonObject.addProperty("entity", globalCdoId.getCdoClass().getName());
        } else {
            jsonObject.addProperty("valueObject", globalCdoId.getCdoClass().getName());
        }

        //cdoId
        if (globalCdoId.getCdoId()!=null){
            jsonObject.add("cdoId", context.serialize(globalCdoId.getCdoId()));
        }

        //owningId & fragment
        if(globalCdoId instanceof ValueObjectId) {
            ValueObjectId valueObjectId = (ValueObjectId)globalCdoId;
            jsonObject.add("ownerId", globalCdoId(valueObjectId.getOwnerId(), context));
            jsonObject.addProperty("fragment", valueObjectId.getFragment());
        }

        return jsonObject;
    }

    @Override
    public Change fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        return null;
    }

    @Override
    public Class getValueType() {
        return null;
    }
}
