package org.javers.core.json;

import com.google.gson.*;
import org.javers.common.validation.Validate;
import org.javers.core.json.typeadapter.joda.LocalDateTimeTypeAdapter;
import org.javers.core.json.typeadapter.joda.LocalDateTypeAdapter;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author bartosz walacik
 * @see JsonConverter
 */
public class JsonConverterBuilder {
    public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final JsonTypeAdapter[] BUILT_IN_ADAPTERS = new JsonTypeAdapter[]{
            new LocalDateTimeTypeAdapter(),
            new LocalDateTypeAdapter()
    };

    private boolean typeSafeValues = false;
    private TypeMapper typeMapper;
    private GlobalIdFactory globalIdFactory;
    private final GsonBuilder gsonBuilder;

    public JsonConverterBuilder() {
        this.gsonBuilder = new GsonBuilder();
        registerJsonTypeAdapters(Arrays.asList(BUILT_IN_ADAPTERS));
    }

    /**
     * When switched to true, all {@link org.javers.core.diff.changetype.Atomic}s
     * are serialized type safely as a type + value pair, for example:
     * <pre>
     * {
     *     "typeAlias": "LocalDate"
     *     "value": "2001-01-01"
     * }
     * </pre>
     * TypeAlias is defaulted to value.class.simpleName.
     * <br/><br/>
     *
     * Useful when serializing polymorfic collections like List or List&lt;Object&gt;
     *
     * @param typeSafeValues default false
     */
    public JsonConverterBuilder typeSafeValues(boolean typeSafeValues) {
        this.typeSafeValues = typeSafeValues;
        return this;
    }

    public JsonConverterBuilder typeMapper(TypeMapper typeMapper){
        this.typeMapper = typeMapper;
        return this;
    }

    public JsonConverterBuilder globalIdFactory(GlobalIdFactory globalIdFactory){
        this.globalIdFactory = globalIdFactory;
        return this;
    }

    /**
     * @param nativeAdapter should be null safe, if not so,
     *                      simply call {@link TypeAdapter#nullSafe()} before registering it
     * @see TypeAdapter
     */
    public JsonConverterBuilder registerNativeTypeAdapter(Type targetType, TypeAdapter nativeAdapter) {
        Validate.argumentsAreNotNull(targetType, nativeAdapter);
        gsonBuilder.registerTypeAdapter(targetType, nativeAdapter);
        return this;
    }

    /**
     * @see JsonSerializer
     */
    public JsonConverterBuilder registerNativeGsonSerializer(Type targetType, JsonSerializer<?> jsonSerializer) {
        Validate.argumentsAreNotNull(targetType, jsonSerializer);
        gsonBuilder.registerTypeAdapter(targetType, jsonSerializer);
        return this;
    }

    /**
     * @see JsonDeserializer
     */
    public JsonConverterBuilder registerNativeGsonDeserializer(Type targetType, JsonDeserializer<?> jsonDeserializer) {
        Validate.argumentsAreNotNull(targetType, jsonDeserializer);
        gsonBuilder.registerTypeAdapter(targetType, jsonDeserializer);
        return this;
    }

    public JsonConverterBuilder registerJsonTypeAdapters(Collection<JsonTypeAdapter> adapters) {
        Validate.argumentIsNotNull(adapters);
        for (JsonTypeAdapter adapter : adapters) {
            registerJsonTypeAdapter(adapter);
        }
        return this;
    }

    /**
     * Maps given {@link JsonTypeAdapter}
     * into pair of {@link JsonDeserializer} and {@link JsonDeserializer}
     * and registers them with this.gsonBuilder
     */
    public JsonConverterBuilder registerJsonTypeAdapter(JsonTypeAdapter adapter) {
        Validate.argumentIsNotNull(adapter);
        for (Class c : (List<Class>)adapter.getValueTypes()){
            registerJsonTypeAdapterForType(c, adapter);
        }
        return this;
    }

    public JsonConverter build() {
        registerJsonTypeAdapter(new AtomicTypeAdapter(typeSafeValues));

        gsonBuilder.serializeNulls()
                   .setPrettyPrinting()
                   .setDateFormat(ISO_DATE_TIME_FORMAT);

        return new JsonConverter(typeMapper, globalIdFactory, gsonBuilder.create());
    }

    private void registerJsonTypeAdapterForType(Type targetType, final JsonTypeAdapter adapter) {
        JsonSerializer jsonSerializer = new JsonSerializer() {
            @Override
            public JsonElement serialize(Object value, Type type, JsonSerializationContext jsonSerializationContext) {
                return adapter.toJson(value, jsonSerializationContext);
            }
        };

        JsonDeserializer jsonDeserializer = new JsonDeserializer() {
            @Override
            public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return adapter.fromJson(jsonElement, jsonDeserializationContext);
            }
        };

        registerNativeGsonSerializer(targetType, jsonSerializer);
        registerNativeGsonDeserializer(targetType, jsonDeserializer);
    }

}
