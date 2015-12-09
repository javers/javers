package org.javers.core.json;

import com.google.gson.*;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Javers is meant to support various persistence stores for
 * any kind of client's data, hence we use JSON format to serialize/deserialize client's domain objects.
 * <br><br>
 *
 * Javers uses <a href='http://sites.google.com/site/gson/'>Gson</a>
 * library which provides neat and pretty JSON representation for well known Java types.
 * <br><br>
 *
 * But sometimes Gson's default JSON representation isn't appropriate for your domain model.
 * This is often the case when dealing with Values like Date or Money.
 * <br><br>
 *
 * If so, you can easily customize Javers serialization/deserialization behaviour
 * by providing plugins for each of your custom type.
 * Javers accepts several kind of plugins:
 * <ul>
 *     <li> {@link BasicStringTypeAdapter} -
 *           extend it if you need to represent Value as single String and don't want to deal with JSON API.
 *           For concrete class example see {@link org.javers.java8support.LocalDateTimeTypeAdapter}.
 *     <li> {@link JsonTypeAdapter} - use it if you need full control over JSON conversion
 *     <li> native Gson {@link TypeAdapter}
 *     <li> native Gson {@link JsonSerializer}
 *     <li> native Gson {@link JsonDeserializer}
 * </ul>
 *
 * Javers provides JsonTypeAdapters for some well known Values like Joda {@link LocalDateTime},
 * Joda {@link LocalDate}, java.time.LocalDate, java.time.LocalDateTime
 *
 * @author bartosz walacik
 */
public class JsonConverter {
    private Gson gson;

    JsonConverter(Gson gson) {
        argumentsAreNotNull(gson);
        this.gson = gson;
    }

    public String toJson(Object value) {
        return gson.toJson(value);
    }

    public JsonElement toJsonElement(Object value) {
        return gson.toJsonTree(value);
    }

    public <T> T fromJson(String json, Class<T> expectedType){
        return gson.fromJson(json, expectedType);
    }

    public Object fromJson(String json, Type expectedType) {
        return gson.fromJson(json, expectedType);
    }

    public <T> T fromJson(JsonElement json, Class<T> expectedType) {
        return gson.fromJson(json, expectedType);
    }
}
