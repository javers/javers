package org.javers.core.json;

import com.google.gson.*;
import org.javers.common.validation.Validate;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.java8support.Java8TypeAdapters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author bartosz walacik
 * @see JsonConverter
 */
public class JsonConverterBuilder {
    private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private boolean typeSafeValues = false;
    private boolean prettyPrint = true;
    private final GsonBuilder gsonBuilder;
    private final List<Class> valueTypes = new ArrayList<>();

    public JsonConverterBuilder() {
        this.gsonBuilder = new GsonBuilder();
        this.gsonBuilder.setExclusionStrategies(new SkipFieldExclusionStrategy());
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

     /**
     * @param prettyPrint default true
     */
    public JsonConverterBuilder prettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
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
     * @since 3.1
     * @see JsonSerializer
     */
    public JsonConverterBuilder registerNativeGsonHierarchySerializer(Class targetType, JsonSerializer<?> jsonSerializer) {
        Validate.argumentsAreNotNull(targetType, jsonSerializer);
        gsonBuilder.registerTypeHierarchyAdapter(targetType, jsonSerializer);
        return this;
    }

    /**
     * @since 3.1
     * @see JsonDeserializer
     */
    public JsonConverterBuilder registerNativeGsonHierarchyDeserializer(Class targetType, JsonDeserializer<?> jsonDeserializer) {
        Validate.argumentsAreNotNull(targetType, jsonDeserializer);
        gsonBuilder.registerTypeHierarchyAdapter(targetType, jsonDeserializer);
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
        adapter.getValueTypes().forEach( c -> registerJsonTypeAdapterForType((Class)c, adapter));
        return this;
    }

    /**
     * @since 3.1
     */
    public JsonConverterBuilder registerJsonAdvancedTypeAdapter(JsonAdvancedTypeAdapter adapter) {
        Validate.argumentIsNotNull(adapter);

        JsonSerializer jsonSerializer = (value, type, jsonSerializationContext) -> adapter.toJson(value, type, jsonSerializationContext);
        JsonDeserializer jsonDeserializer = (jsonElement, type, jsonDeserializationContext) -> adapter.fromJson(jsonElement, type, jsonDeserializationContext);

        registerNativeGsonHierarchySerializer(adapter.getTypeSuperclass(), jsonSerializer);
        registerNativeGsonHierarchyDeserializer(adapter.getTypeSuperclass(), jsonDeserializer);

        return this;
    }

    public List<Class> getValueTypes() {
        return Collections.unmodifiableList(valueTypes);
    }

    public JsonConverter build() {
        registerJsonTypeAdapters(UtilTypeCoreAdapters.adapters());
        registerJsonTypeAdapters(Java8TypeAdapters.adapters());
        registerJsonTypeAdapter(new AtomicTypeAdapter(typeSafeValues));

        if (prettyPrint){
            gsonBuilder.setPrettyPrinting();
        }

        gsonBuilder.serializeNulls()
                   .setDateFormat(ISO_DATE_TIME_FORMAT);

        return new JsonConverter(gsonBuilder.create());
    }

    private void registerJsonTypeAdapterForType(Class targetType, final JsonTypeAdapter adapter) {
        valueTypes.add(targetType);
        JsonSerializer jsonSerializer = (value, type, jsonSerializationContext) -> adapter.toJson(value, jsonSerializationContext);
        JsonDeserializer jsonDeserializer = (jsonElement, type, jsonDeserializationContext) -> adapter.fromJson(jsonElement, jsonDeserializationContext);

        registerNativeGsonSerializer(targetType, jsonSerializer);
        registerNativeGsonDeserializer(targetType, jsonDeserializer);
    }

    private static class SkipFieldExclusionStrategy implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> clazz) {
            return clazz.getAnnotation(DiffIgnore.class) != null;
        }

        public boolean shouldSkipField(FieldAttributes field) {
            return field.getAnnotation(DiffIgnore.class) != null;
        }
    }
}
