package org.javers.core.json;

import com.google.gson.*;
import org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

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
 * This is often the case when dealing with Values like Date or Money.
 * <br/><br/>
 *
 * If so, you can easily customize Javers serialization/deserialization behaviour
 * by providing plugins for each of your custom type.
 * Javers accepts several kind of plugins:
 * <ul>
 *     <li/> {@link BasicStringTypeAdapter} -
 *           extend it if you need to represent unwrap as single String and don't want to deal with JSON API.
 *           For concrete class example see {@link LocalDateTimeTypeAdapter}.
 *     <li/> {@link JsonTypeAdapter} - use it if you need full control over JSON conversion
 *     <li/> native Gson {@link TypeAdapter}
 *     <li/> native Gson {@link JsonSerializer}
 *     <li/> native Gson {@link JsonDeserializer}
 * </ul>
 *
 * Javers provides JsonTypeAdapter's for some well known Value like {@link LocalDateTime}.
 * Those adapters are included by default in Javers setup, see {@link JsonConverterBuilder#BUILT_IN_ADAPTERS}
 * <br/>
 *
 * @author bartosz walacik
 */
public class JsonConverter {
    public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private Gson gson;
    private final GsonBuilder gsonBuilder;

    JsonConverter() {
        gsonBuilder = new GsonBuilder();
    }

    void initialize() {
        gson = gsonBuilder.serializeNulls()
                          .setPrettyPrinting()
                          .setDateFormat(ISO_DATE_TIME_FORMAT)
                          .create();
    }

    /**
     * @param nativeAdapter should be null safe, if not so,
     *                      simply call {@link TypeAdapter#nullSafe()} before registering it
     * @see TypeAdapter
     */
    void registerNativeGsonTypeAdapter(Type targetType, TypeAdapter nativeAdapter) {
        gsonBuilder.registerTypeAdapter(targetType, nativeAdapter);
    }

    /**
     * @see JsonSerializer
     */
    void registerNativeGsonSerializer(Type targetType, JsonSerializer<?> jsonSerializer){
        gsonBuilder.registerTypeAdapter(targetType, jsonSerializer);

    }

    /**
     * @see JsonDeserializer
     */
    void registerNativeGsonDeserializer(Type targetType, JsonDeserializer<?> jsonDeserializer){
        gsonBuilder.registerTypeAdapter(targetType, jsonDeserializer);
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
    void registerJsonTypeAdapter(JsonTypeAdapter adapter) {
        for (Class c : (List<Class>)adapter.getValueTypes()){
            registerJsonTypeAdapter(c, adapter);
        }
    }

    public String toJson(Object value) {
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

    void registerJsonTypeAdapter(Type targetType, final JsonTypeAdapter adapter) {
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
