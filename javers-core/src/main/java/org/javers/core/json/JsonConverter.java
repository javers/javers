package org.javers.core.json;

import com.google.gson.*;
import org.javers.common.validation.Validate;
import org.javers.core.json.typeadapter.joda.LocalDateTimeTypeAdapter;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.TypeMapper;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Type;

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
 *           For concrete class example see {@link LocalDateTimeTypeAdapter}.
 *     <li> {@link JsonTypeAdapter} - use it if you need full control over JSON conversion
 *     <li> native Gson {@link TypeAdapter}
 *     <li> native Gson {@link JsonSerializer}
 *     <li> native Gson {@link JsonDeserializer}
 * </ul>
 *
 * Javers provides JsonTypeAdapters for some well known Values like {@link LocalDateTime}.
 * Those adapters are included by default in Javers setup, see {@link JsonConverterBuilder#BUILT_IN_ADAPTERS}
 * <br>
 *
 * @author bartosz walacik
 */
public class JsonConverter {
    private Gson gson;
    private final TypeMapper typeMapper;

    JsonConverter(TypeMapper typeMapper, Gson gson) {
        Validate.argumentsAreNotNull(typeMapper, gson);
        this.typeMapper = typeMapper;
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

    public Object deserializePropertyValue(Property property, String valueAsJson){
        Type dehydratedPropertyType = typeMapper.getDehydratedType(property.getGenericType());
        return fromJson(valueAsJson, dehydratedPropertyType);
    }

}
