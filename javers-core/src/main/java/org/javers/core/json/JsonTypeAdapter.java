package org.javers.core.json;

import com.google.gson.JsonElement;
import org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter;
import java.lang.reflect.Type;

/**
 * Implement JsonTypeAdapter to add custom JSON serialization and deserialization behaviour,
 * depending on your domain model.
 * <br/><br/>
 *
 * Value Object eligible for deserialization should have a no-argument constructor (public or private).
 * <br/><br/>
 *
 * Implementation shouldn't take care about nulls (nulls are handled by Gson engine)
 * <br/><br/>
 *
 * For implementation example see {@link LocalDateTimeTypeAdapter}.
 * <br/><br/>
 *
 * @see LocalDateTimeTypeAdapter
 * @author bartosz walacik
 */
public interface JsonTypeAdapter<T> {

    /**
     * @param json not null and not JsonNull
     */
    T fromJson(JsonElement json);

    /**
     * @param sourceValue not null
     */
    JsonElement toJson(T sourceValue);

    /**
     * target Class, for ex. LocalDateTime
     */
    Type getType();
}
