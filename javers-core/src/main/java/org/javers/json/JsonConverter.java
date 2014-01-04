package org.javers.json;

import com.google.gson.*;
import org.javers.json.typeAdapter.LocalDateTimeTypeAdapter;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

/**
 * Javers is meant to support various persistence stores for
 * any kind of client's data, hence we use JSON format to serialize/deserialize client's domain objects.
 * <br/><br/>
 *
 * Javers uses <a href='http://sites.google.com/site/gson/'>Gson</a>
 * library which provides neat and pretty JSON representation for well known Java types.
 * <br/><br/>
 *
 * But sometimes Gson's default JSON representation isn't appropriate for your domain model.
 * This is often the case when dealing with Value Objects like Date or Money.
 * <br/><br/>
 *
 * If so, you can easily customize Javers serialization/deserialization behaviour
 * by providing plugins for each of your custom type.
 * Javers accepts several kind of plugins:
 * <ul>
 *     <li/> {@link BasicStringTypeAdapter} -
 *           extend it if you need to represent value as single String and don't want to deal with JSON API.
 *           For implementation example see {@link org.javers.json.typeAdapter.LocalDateTimeTypeAdapter}.
 *     <li/> {@link JsonTypeAdapter} - use it if you need full control over JSON conversion
 *     <li/> native Gson {@link TypeAdapter} //TODO not implemented yet
 * </ul>
 *
 * Javers provides JsonTypeAdapter's for some well known Value Object like {@link org.joda.time.LocalDateTime}.
 * Those adapters are included by default in Javers setup, see {@link #BUILT_IN_ADAPTERS}
 * <br/>
 *
 * @author bartosz walacik
 */
public class JsonConverter {
    private Gson gson;
    private GsonBuilder gsonBuilder;

    private static final JsonTypeAdapter[] BUILT_IN_ADAPTERS = new JsonTypeAdapter[]{new LocalDateTimeTypeAdapter()};

    JsonConverter() {
        gsonBuilder = new GsonBuilder().serializeNulls();
        registerJsonTypeAdapters(Arrays.asList(BUILT_IN_ADAPTERS));
    }

    void initialize() {
        gson = gsonBuilder.create();
    }

    /**
     * @see GsonBuilder#registerTypeAdapter(Type, Object)
     */
    void registerNativeTypeAdapter(Type targetType, TypeAdapter nativeAdapter) {
        gsonBuilder.registerTypeAdapter(targetType, nativeAdapter);
    }

    void registerJsonTypeAdapters(Collection<JsonTypeAdapter> adapters){
        for (JsonTypeAdapter adapter : adapters) {
            registerJsonTypeAdapter(adapter);
        }
    }

    /**
     * Maps given {@link JsonTypeAdapter}
     * into pair of {@link JsonDeserializer} and {@link JsonDeserializer}
     * and registers them with this.gsonBuilder
     */
    void registerJsonTypeAdapter(final JsonTypeAdapter adapter) {
        JsonSerializer jsonSerializer = new JsonSerializer() {
            @Override
            public JsonElement serialize(Object value, Type type, JsonSerializationContext jsonSerializationContext) {
                return adapter.toJson(value);
            }
        };

        JsonDeserializer jsonDeserializer = new JsonDeserializer() {
            @Override
            public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return adapter.fromJson(jsonElement);
            }
        };

        gsonBuilder.registerTypeAdapter(adapter.getType(), jsonSerializer);
        gsonBuilder.registerTypeAdapter(adapter.getType(), jsonDeserializer);
    }

    public String toJson(Object value) {
        checkState();
        return gson.toJson(value);
    }

    public String toJson(Object value, Type requiredType) {
        checkState();
        return gson.toJson(value);
    }

    public <T> T fromJson(String json, Class<T> expectedType) {
        checkState();
        return gson.fromJson(json,expectedType);
    }

    private void checkState() {
        if (gson == null) {
            throw new IllegalStateException("JsonConverter not initialized");
        }
    }
}
