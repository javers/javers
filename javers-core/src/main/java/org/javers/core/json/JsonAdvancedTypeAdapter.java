package org.javers.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

/**
 * Advanced variant of {@link JsonTypeAdapter}.<br/>
 * Can be used to write polymorphic/generic type adapters.
 * <br/><br/>
 *
 * Provides additional argument: <code>Type typeOfT</code>  &mdash; a runtime type of a serialized/deserialized object
 *
 * @author bartosz.walacik
 */
public interface JsonAdvancedTypeAdapter<T> {

    T fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context);

    JsonElement toJson(T sourceValue, Type typeOfT, JsonSerializationContext context);

    Class<T> getTypeSuperclass();
}
