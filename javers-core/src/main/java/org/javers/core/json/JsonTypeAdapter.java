package org.javers.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter;
import org.javers.core.metamodel.type.ValueType;

/**
 * Javers abstraction over native
 * <a href="http://code.google.com/p/google-gson/">Gson</a> TypeAdapter.
 * <p/>
 *
 * Implement JsonTypeAdapter to add custom JSON serialization and deserialization behaviour,
 * depending on your domain model.
 * <p/>
 *
 * {@link ValueType} eligible for deserialization should have a no-argument constructor (public or private).
 * <p/>
 *
 * Implementation shouldn't take care about nulls (nulls are handled by Gson engine)
 * <p/>
 *
 * For implementation example see {@link LocalDateTimeTypeAdapter}.
 * <p/>
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
     * target class of {@link ValueType}, for ex. Money.class
     */
    Class getValueType();
}
