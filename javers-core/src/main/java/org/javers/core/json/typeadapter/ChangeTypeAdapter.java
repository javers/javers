package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.map.*;
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
            NewObject.class, ObjectRemoved.class, ValueChange.class, ReferenceChange.class, MapChange.class};

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

        if (change instanceof MapChange) {
            appendBody((MapChange) change, jsonObject, context);
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

    private void appendBody(MapChange change, JsonObject toJson, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();

        for (EntryChange entryChange : change.getEntryChanges()) {
            JsonObject entryElement = new JsonObject();
            entryElement.addProperty("entryChangeType", entryChange.getClass().getSimpleName());

            if (entryChange instanceof EntryAddOrRemove) {
                Object key =   ((EntryAddOrRemove) entryChange).getKey();
                Object value = ((EntryAddOrRemove) entryChange).getValue();

                entryElement.add("key", context.serialize(key));
                entryElement.add("value", context.serialize(value));
            }

            if (entryChange instanceof EntryValueChanged) {
                EntryValueChanged entryValueChanged = (EntryValueChanged)entryChange;
                entryElement.add("key", context.serialize(entryValueChanged.getKey()));
                entryElement.add("leftValue", context.serialize(entryValueChanged.getLeftValue()));
                entryElement.add("rightValue", context.serialize(entryValueChanged.getRightValue()));

            }
            jsonArray.add(entryElement);
        }
        toJson.add("entryChanges", jsonArray);
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
