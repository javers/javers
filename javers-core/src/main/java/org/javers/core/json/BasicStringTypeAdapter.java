package org.javers.core.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Convenient abstract implementation of {@link JsonTypeAdapter}.
 * Extend it if you need to represent value (T) as single String and don't want to deal with JSON API.
 * <br/><br/>
 *
 * Implementation shouldn't take care about nulls
 * br/><br/>
 *
 * For implementation example see {@link org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter}.
 * <br/><br/>
 *
 * @author bartosz walacik
 */
public abstract class BasicStringTypeAdapter<T> implements JsonTypeAdapter<T> {

    /**
     * Example serialization for LocalDateTime:
     * <pre>
     * public String serialize(LocalDateTime sourceValue) {
     *     return ISO_FORMATTER.print(sourceValue);
     * }
     * </pre>
     * @param sourceValue not null
     */
    public abstract String serialize(T sourceValue);

    /**
     * Example deserialization for LocalDateTime:
     * <pre>
     * public LocalDateTime deserialize(String serializedValue) {
     *     return ISO_FORMATTER.parseLocalDateTime(serializedValue);
     * }
     * </pre>
     *
     * @param serializedValue not null
     */
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
