package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Map;

class CommitPropertiesConverter {
    private static final String PROPERTY_KEY_FIELD = "key";
    private static final String PROPERTY_VALUE_FIELD = "value";

    static Map<String, String> fromJson(JsonElement json) {
        Map<String, String> properties = new HashMap<>();
        if (json != null) {
            for (JsonElement jsonElement : json.getAsJsonArray()) {
                JsonObject propertyObject = jsonElement.getAsJsonObject();
                String key = propertyObject.get(PROPERTY_KEY_FIELD).getAsString();
                String value = propertyObject.get(PROPERTY_VALUE_FIELD).getAsString();
                properties.put(key, value);
            }
        }
        return properties;
    }

    static JsonElement toJson(Map<String, String> properties) {
        JsonArray propertiesArray = new JsonArray();
        if (properties != null) {
            for (Map.Entry<String, String> metadata : properties.entrySet()) {
                String value = metadata.getValue();
                if (value == null) {
                    continue;
                }

                JsonObject propertyObject = new JsonObject();
                propertyObject.add(PROPERTY_KEY_FIELD, new JsonPrimitive(metadata.getKey()));
                propertyObject.add(PROPERTY_VALUE_FIELD, new JsonPrimitive(value));
                propertiesArray.add(propertyObject);
            }
        }
        return propertiesArray;
    }
}
