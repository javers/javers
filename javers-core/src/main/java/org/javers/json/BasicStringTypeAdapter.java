package org.javers.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Convenient abstract implementation of {@link JsonTypeAdapter}.
 * Extend it if you need to represent value (T) as single String and don't want to deal with JSON API.
 * <br/><br/>
 *
 * For implementation example see {@link org.javers.json.typeAdapter.LocalDateTimeTypeAdapter}.
 * <br/><br/>
 *
 * @author bartosz walacik
 */
public abstract class BasicStringTypeAdapter<T> implements JsonTypeAdapter<T> {

    public abstract String serialize(T sourceValue);

    public abstract T deserialize(String serializedValue);

    @Override
    public T fromJson(JsonElement json) {
        return deserialize(json.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement toJson(T sourceValue) {
        return new JsonPrimitive(serialize(sourceValue));
    }
}
