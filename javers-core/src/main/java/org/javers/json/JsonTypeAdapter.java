package org.javers.json;

import com.google.gson.JsonElement;

/**
 * Implement JsonTypeAdapter to add custom JSON serialization and deserialization behaviour,
 * depending on your domain model.
 * <br/><br/>
 *
 * Value Object eligible for deserialization should have a no-argument constructor (public or private).
 *
 * @see org.javers.json.typeAdapter.LocalDateTimeTypeAdapter
 * @author bartosz walacik
 */
public interface JsonTypeAdapter<T> {
    T fromJson(JsonElement json);

    JsonElement toJson(T sourceValue);

    Class<T> getType();
}
