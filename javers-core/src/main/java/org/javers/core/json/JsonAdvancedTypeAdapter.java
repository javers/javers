package org.javers.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

/**
 * Advanced variant of {@link JsonTypeAdapter}
 *
 * @author bartosz.walacik
 */
public interface JsonAdvancedTypeAdapter<T> {

    T fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context);

    JsonElement toJson(T sourceValue, Type typeOfT, JsonSerializationContext context);

    Class<T> getTypeSuperclass();
}
