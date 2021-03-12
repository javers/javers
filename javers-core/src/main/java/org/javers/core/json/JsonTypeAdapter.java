package org.javers.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.javers.core.metamodel.type.CustomType;
import org.javers.core.metamodel.type.ValueType;

import java.util.List;

/**
 * Javers abstraction over native
 * <a href="http://code.google.com/p/google-gson/">Gson</a> TypeAdapter.
 * <p/>
 *
 * Implement a JsonTypeAdapter to add custom JSON serialization and deserialization behaviour,
 * depending on your domain model.
 * <p/>
 *
 * Implementation shouldn't take care about nulls (nulls are handled by Gson engine).
 * For a concrete adapter implementation example see {@link org.javers.java8support.LocalDateTimeTypeAdapter}.
 * <p/>
 *
 * Convenient template classes are available, see {@link BasicStringTypeAdapter}
 * <p/>
 *
 * <b>Usage with Vanilla Javers</b>
 *
 * <pre>Javers javers = JaversBuilder.javers()
 *                  .registerValueTypeAdapter(new MyTypeAdapter())
 *                  .build();
 * </pre>
 *
 * <b>Usage with Javers Spring Boot starters</b>
 * <br/>
 * Simply register your JSON type adapters as Spring beans.
 * <br/>
 *
 * @param <T> user type, mapped to {@link ValueType} or {@link CustomType}
 * @see JsonConverter
 * @see JsonAdvancedTypeAdapter
 * @author bartosz walacik
 */
public interface JsonTypeAdapter<T> {

    /**
     * @param json not null and not JsonNull
     * @param jsonDeserializationContext use it to invoke default deserialization on the specified object
     */
    T fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext);

    /**
     * @param sourceValue not null
     * @param jsonSerializationContext use it to invoke default serialization on the specified object
     */
    JsonElement toJson(T sourceValue, JsonSerializationContext jsonSerializationContext);

    /**
     * Target class (or classes), typically {@link ValueType} or {@link CustomType}.
     * <br/>
     * Each target  class should have a no-argument constructor (public or private).
     * <p/>
     *
     * If adapter is designed to handle single class, should return a List with one element &mdash; a supported class.<br/
     * If adapter is polymorphic, should return all supported classes.
     */
    List<Class> getValueTypes();
}
