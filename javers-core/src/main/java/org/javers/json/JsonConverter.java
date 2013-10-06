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
 *           extend it if you need to represent value as single String and don't want to deal with JSON API
 *     <li/> {@link JsonTypeAdapter} - use it if you need full control over JSON conversion
 *     <li/> native Gson {@link TypeAdapter} //TODO not implemented yet
 * </ul>
 *
 * Javers provides JsonTypeAdapter's for some well known Value Object like {@link org.joda.time.LocalDateTime}.
 * Those adapters are included by default in Javers setup, see {@link #BUILT_IN_ADAPTERS}
 * <br/>
 *
 * //TODO add nullSafe story
 *
 * @author bartosz walacik
 */
public class JsonConverter {
    private Gson gson;

    private static final JsonTypeAdapter[] BUILT_IN_ADAPTERS = new JsonTypeAdapter[]{new LocalDateTimeTypeAdapter()};

    public JsonConverter() {
        gson = initGsonBuilder().create();
    }

    public JsonConverter(Collection<JsonTypeAdapter> adapters) {
        GsonBuilder gsonBuilder = initGsonBuilder();

        registerAdapters(gsonBuilder, adapters);

        gson = gsonBuilder.create();
    }

    private GsonBuilder initGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();

        registerAdapters(gsonBuilder, Arrays.asList(BUILT_IN_ADAPTERS));

        return gsonBuilder;
    }

    private void registerAdapters(GsonBuilder gsonBuilder, Collection<JsonTypeAdapter> adapters){
        for (JsonTypeAdapter adapter : adapters) {
            registerSerializerAndDeserializer(gsonBuilder, adapter);
        }
    }

    /**
     * Maps interface of given {@link JsonTypeAdapter}
     * into pair of {@link JsonDeserializer} and {@link JsonDeserializer}
     * and registers them with given gsonBuilder
     */
    private void registerSerializerAndDeserializer(GsonBuilder gsonBuilder, final JsonTypeAdapter adapter) {
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
        return gson.toJson(value);
    }

    public <T> T fromJson(String json, Class<T> expectedType) {
        return gson.fromJson(json,expectedType);
    }
}
