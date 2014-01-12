package org.javers.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter;

import java.lang.reflect.Type;

/**
 * Implement JsonTypeAdapter to add custom JSON serialization and deserialization behaviour,
 * depending on your domain model.
 * <br/><br/>
 *
 * {@link org.javers.model.mapping.type.ValueType} eligible for deserialization should have a no-argument constructor (public or private).
 * <br/><br/>
 *
 * Implementation shouldn't take care about nulls (nulls are handled by Gson engine)
 * <br/><br/>
 *
 * For implementation example see {@link LocalDateTimeTypeAdapter}.
 * <br/><br/>
 *
 * @see LocalDateTimeTypeAdapter
 * @see JsonConverter
 * @author bartosz walacik
 */
public interface JsonTypeAdapter<T> {

    /**
     * @param json not null and not JsonNull
     * @param jsonDeserializationContext use it to invoke default deserialization on the specified object
     */
    T fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext);

    /**
     * @param sourceValue not null
     * @param jsonSerializationContext use it to invoke default serialization on the specified object
     */
    JsonElement toJson(T sourceValue, JsonSerializationContext jsonSerializationContext);

    /**
     * target Class, for ex. LocalDateTime
     */
    Type getType();
}
