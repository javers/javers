package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.map.*;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.Entity;

import java.lang.reflect.Type;

/**
 * Change to JSON serializer
 *
 * @author bartosz walacik
 */
public class ChangeTypeAdapter implements JsonTypeAdapter<Change> {
    public static final Type[] SUPPORTED = {
            NewObject.class, ObjectRemoved.class, ValueChange.class, ReferenceChange.class, MapChange.class};

    private GlobalCdoIdAdapter globalCdoIdAdapter;

    public ChangeTypeAdapter(GlobalCdoIdAdapter globalCdoIdAdapter) {
        this.globalCdoIdAdapter = globalCdoIdAdapter;
    }

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
        toJson.add("leftReference",  globalCdoIdAdapter.toJson(change.getLeftReference(), context));
        toJson.add("rightReference", globalCdoIdAdapter.toJson(change.getRightReference(), context));
    }

    private void appendBody(ValueChange change, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("leftValue", context.serialize(change.getWrappedLeftValue()));
        toJson.add("rightValue", context.serialize(change.getWrappedRightValue()));
    }

    private void appendBody(MapChange change, JsonObject toJson, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();

        for (EntryChange entryChange : change.getEntryChanges()) {
            JsonObject entryElement = new JsonObject();
            entryElement.addProperty("entryChangeType", entryChange.getClass().getSimpleName());

            if (entryChange instanceof EntryAddOrRemove) {
                EntryAddOrRemove entry = (EntryAddOrRemove)entryChange;

                entryElement.add("key", context.serialize(entry.getWrappedKey()));
                entryElement.add("value", context.serialize(entry.getWrappedValue()));
            }

            if (entryChange instanceof EntryValueChanged) {
                EntryValueChanged entry = (EntryValueChanged)entryChange;
                entryElement.add("key", context.serialize(entry.getWrappedKey()));
                entryElement.add("leftValue", context.serialize(entry.getWrappedLeftValue()));
                entryElement.add("rightValue", context.serialize(entry.getWrappedRightValue()));

            }
            jsonArray.add(entryElement);
        }
        toJson.add("entryChanges", jsonArray);
    }

    private void appendGlobalId(GlobalCdoId globalCdoId, JsonObject toJson, JsonSerializationContext context) {
        toJson.add("globalCdoId", globalCdoIdAdapter.toJson(globalCdoId, context));
    }

    @Override
    public Change fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Class getValueType() {
        return null;
    }
}
