package org.javers.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

public abstract class BasicStringSuperTypeAdapter<T> implements JsonAdvancedTypeAdapter<T> {

    public abstract String serialize(T sourceValue);

    public abstract T deserialize(String serializedValue);

    @Override
    public T fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext jsonDeserializationContext ) {
        return deserialize(json.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement toJson(T sourceValue, Type typeOfT, JsonSerializationContext context) {
        return new JsonPrimitive(serialize(sourceValue));
    }
}
