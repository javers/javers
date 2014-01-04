package org.javers.json;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import org.javers.common.validation.Validate;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @see JsonConverter
 * @author bartosz walacik
 */
public class JsonConverterBuilder {
    private JsonConverter jsonConverter;

    /**
     * use static jsonConverter().build();
     */
    private JsonConverterBuilder() {
        jsonConverter = new JsonConverter();
    }

    public static JsonConverterBuilder jsonConverter() {
        return new JsonConverterBuilder();
    }

    /**
     * @see GsonBuilder#registerTypeAdapter(Type, Object)
     */
    public JsonConverterBuilder registerNativeTypeAdapter(Type targetType, TypeAdapter nativeAdapter) {
        Validate.argumentsAreNotNull(targetType, nativeAdapter);
        jsonConverter.registerNativeTypeAdapter(targetType, nativeAdapter);
        return this;
    }

    public JsonConverterBuilder registerJsonTypeAdapter(JsonTypeAdapter adapter){
        Validate.argumentIsNotNull(adapter);
        jsonConverter.registerJsonTypeAdapter(adapter);
        return this;
    }

    public JsonConverterBuilder registerJsonTypeAdapters(Collection<JsonTypeAdapter> adapters){
        Validate.argumentIsNotNull(adapters);
        jsonConverter.registerJsonTypeAdapters(adapters);
        return this;
    }

    public JsonConverter build() {
        jsonConverter.initialize();
        return jsonConverter;
    }
}
