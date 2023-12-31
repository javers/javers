package org.javers.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

public abstract class BasicStringSuperTypeAdapter<T> implements JsonAdvancedTypeAdapter<T> {

    /**
     * Example serialization for LocalDateTime:
     * <pre>
     * public String serialize(LocalDateTime sourceValue) {
     *     return ISO_DATE_TIME_FORMATTER.print(sourceValue);
     * }
     * </pre>
     * @param sourceValue not null
     */
    public abstract String serialize(T sourceValue);

    /**
     * Example deserialization for LocalDateTime:
     * <pre>
     * public LocalDateTime deserialize(String serializedValue) {
     *     return ISO_DATE_TIME_FORMATTER.parseLocalDateTime(serializedValue);
     * }
     * </pre>
     *
     * @param serializedValue not null
     */
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
