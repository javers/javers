package org.javers.core.json.typeadapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.map.EntryAddOrRemove;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.EntryValueChanged;
import org.javers.core.diff.changetype.map.MapChange;

public class MapChangeTypeAdapter extends AbstractTypeAdapter<MapChange> {

  @Override
  public JsonElement toJson(MapChange change, JsonSerializationContext context) {
    final JsonObject jsonObject = createJsonObject(change, context);

    appendBody(change, jsonObject, context);

    return jsonObject;
  }

  private void appendBody(MapChange change, JsonObject toJson, JsonSerializationContext context) {
    JsonArray jsonArray = new JsonArray();

    for (EntryChange entryChange : change.getEntryChanges()) {
      JsonObject entryElement = new JsonObject();
      entryElement.addProperty("entryChangeType", entryChange.getClass().getSimpleName());

      if (entryChange instanceof EntryAddOrRemove) {
        EntryAddOrRemove entry = (EntryAddOrRemove) entryChange;

        entryElement.add("key", context.serialize(entry.getWrappedKey()));
        entryElement.add("value", context.serialize(entry.getWrappedValue()));
      }

      if (entryChange instanceof EntryValueChanged) {
        EntryValueChanged entry = (EntryValueChanged) entryChange;
        entryElement.add("key", context.serialize(entry.getWrappedKey()));
        entryElement.add("leftValue", context.serialize(entry.getWrappedLeftValue()));
        entryElement.add("rightValue", context.serialize(entry.getWrappedRightValue()));

      }
      jsonArray.add(entryElement);
    }
    toJson.add("entryChanges", jsonArray);
  }
}
