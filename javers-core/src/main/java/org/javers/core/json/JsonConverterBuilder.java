package org.javers.core.json;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import org.javers.common.validation.Validate;
import org.javers.core.json.typeadapter.*;
import org.javers.core.json.typeadapter.AtomicTypeAdapter;
import org.javers.core.json.typeadapter.ChangeTypeAdapter;
import org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter;
import org.javers.core.json.typeadapter.LocalDateTypeAdapter;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author bartosz walacik
 * @see JsonConverter
 */
public class JsonConverterBuilder {

  private static final JsonTypeAdapter[] BUILT_IN_ADAPTERS = new JsonTypeAdapter[]{
      new LocalDateTimeTypeAdapter(),
      new LocalDateTypeAdapter(),
      new MapChangeTypeAdapter(),
      new NewObjectTypeAdapter(),
      new ObjectRemovedTypeAdapter(),
      new ReferenceChangeTypeAdapter(),
      new ValueChangeTypeAdapter()
  };

  private boolean typeSafeValues = false;

  private final JsonConverter jsonConverter;

  /**
   * choose between new JsonConverterBuilder() or static jsonConverter()
   */
  public JsonConverterBuilder() {
    jsonConverter = new JsonConverter();
    jsonConverter.registerJsonTypeAdapters(Arrays.asList(BUILT_IN_ADAPTERS));
  }

    /**
     * When switched to true, all {@link org.javers.core.diff.changetype.Atomic}s are serialized type safely as a pair, fo example:
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
  public static JsonConverterBuilder jsonConverter() {
    return new JsonConverterBuilder();
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
  public JsonConverterBuilder registerNativeGsonSerializer(Type targetType, JsonSerializer<?> jsonSerializer) {
    Validate.argumentsAreNotNull(targetType, jsonSerializer);
    jsonConverter.registerNativeGsonSerializer(targetType, jsonSerializer);
    return this;
  }

  /**
   * @see JsonDeserializer
   */
  public JsonConverterBuilder registerNativeGsonDeserializer(Type targetType, JsonDeserializer<?> jsonDeserializer) {
    Validate.argumentsAreNotNull(targetType, jsonDeserializer);
    jsonConverter.registerNativeGsonDeserializer(targetType, jsonDeserializer);
    return this;
  }

  public JsonConverterBuilder registerJsonTypeAdapter(JsonTypeAdapter adapter) {
    Validate.argumentIsNotNull(adapter);
    jsonConverter.registerJsonTypeAdapter(adapter);
    return this;
  }

  public JsonConverterBuilder registerJsonTypeAdapters(Collection<JsonTypeAdapter> adapters) {
    Validate.argumentIsNotNull(adapters);
    jsonConverter.registerJsonTypeAdapters(adapters);
    return this;
  }

        jsonConverter.registerJsonTypeAdapter(new AtomicTypeAdapter(typeSafeValues));
  public JsonConverter build() {

    jsonConverter.registerJsonTypeAdapter(new ValueTypeAdapter(typeSafeValues));

    jsonConverter.initialize();
    return jsonConverter;
  }

}
