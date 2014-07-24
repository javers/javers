package org.javers.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter;

/**
 * Convenient abstract implementation of {@link JsonTypeAdapter}.
 * Extend it if you need to represent unwrap (T) as single String and don't want to deal with Gson API.
 * <br><br>
 *
 * Implementation shouldn't take care about nulls (nulls are handled by Gson engine)
 * <br><br>
 *
 * For concrete class example see {@link LocalDateTimeTypeAdapter}.
 * <br><br>
 *
 * @author bartosz walacik
 */
public abstract class BasicStringTypeAdapter<T> extends JsonTypeAdapterTemplate<T> {

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
    public T fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext ) {
        return deserialize(json.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement toJson(T sourceValue, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(serialize(sourceValue));
    }
}
