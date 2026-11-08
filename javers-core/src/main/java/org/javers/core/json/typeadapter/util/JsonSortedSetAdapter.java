package org.javers.core.json.typeadapter.util;

import com.google.gson.*;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.snapshot.JsonSortedCollection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class JsonSortedSetAdapter extends JsonTypeAdapterTemplate<JsonSortedCollection> {

    @Override
    public JsonElement toJson(JsonSortedCollection sourceValue, JsonSerializationContext jsonSerializationContext) {
        var col = sourceValue.getSourceCollection();

        List<JsonElement> elements = col.stream()
                .map(e -> jsonSerializationContext.serialize(e)).collect(Collectors.toList());

        elements.sort(Comparator.comparing(JsonElement::toString));

        JsonArray result = new JsonArray();
        elements.forEach(result::add);
        return result;
    }

    @Override
    public JsonSortedCollection fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Class getValueType() {
        return JsonSortedCollection.class;
    }

}
