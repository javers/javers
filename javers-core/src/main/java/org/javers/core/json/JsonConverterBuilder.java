package org.javers.core.json;

import com.google.gson.*;
import org.javers.common.validation.Validate;
import org.javers.core.json.typeadapter.ChangeTypeAdapter;
import org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter;
import org.javers.core.json.typeadapter.LocalDateTypeAdapter;
import org.javers.core.json.typeadapter.ValueTypeAdapter;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

/**
 * @see JsonConverter
 * @author bartosz walacik
 */
public class JsonConverterBuilder {
    private static final JsonTypeAdapter[] BUILT_IN_ADAPTERS = new JsonTypeAdapter[] {
            new LocalDateTimeTypeAdapter(),
            new LocalDateTypeAdapter()
    };

    private boolean typeSafeValues = false;

    private JsonConverter jsonConverter;

    /**
     * choose between new JsonConverterBuilder() or static jsonConverter()
     */
    public JsonConverterBuilder() {
        jsonConverter = new JsonConverter();
        jsonConverter.registerJsonTypeAdapters(Arrays.asList(BUILT_IN_ADAPTERS));
        registerChangeTypeAdapter();
    }

    public static JsonConverterBuilder jsonConverter() {
        return new JsonConverterBuilder();
    }

    /**
     * When switched to true, all {@link org.javers.core.diff.changetype.Value}s are serialized type safely as a pair, fo example:
     * <pre>
     * {
     *     "typeAlias": "LocalDate"
     *     "value": "2001-01-01"
     * }
     * </pre>
     * TypeAlias is defaulted to value.class.simpleName.
     * <p/>
     *
     * Useful when serializing polymorfic collections like List or List&lt;Object&gt;
     *
     * @param typeSafeValues default false
     */
    public JsonConverterBuilder typeSafeValues(boolean typeSafeValues){
        this.typeSafeValues = typeSafeValues;
        return this;
    }

    /**
     * @see JsonConverter#registerNativeGsonTypeAdapter(Type, TypeAdapter)
     */
    public JsonConverterBuilder registerNativeTypeAdapter(Type targetType, TypeAdapter nativeAdapter) {
        Validate.argumentsAreNotNull(targetType, nativeAdapter);
        jsonConverter.registerNativeGsonTypeAdapter(targetType, nativeAdapter);
        return this;
    }

    /**
     * @see JsonSerializer
     */
    public JsonConverterBuilder registerNativeGsonSerializer(Type targetType, JsonSerializer<?> jsonSerializer){
        Validate.argumentsAreNotNull(targetType, jsonSerializer);
        jsonConverter.registerNativeGsonSerializer(targetType, jsonSerializer);
        return this;
    }

    /**
     * @see JsonDeserializer
     */
    public  JsonConverterBuilder registerNativeGsonDeserializer(Type targetType, JsonDeserializer<?> jsonDeserializer){
        Validate.argumentsAreNotNull(targetType, jsonDeserializer);
        jsonConverter.registerNativeGsonDeserializer(targetType, jsonDeserializer);
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

        jsonConverter.registerJsonTypeAdapter(new ValueTypeAdapter(typeSafeValues));

        jsonConverter.initialize();
        return jsonConverter;
    }

    private void registerChangeTypeAdapter() {
        ChangeTypeAdapter changeTypeAdapter = new ChangeTypeAdapter();

        for (Type targetType : ChangeTypeAdapter.SUPPORTED) {
            jsonConverter.registerJsonTypeAdapter(targetType, changeTypeAdapter);
        }
    }
}
