package org.javers.core.json;

import com.google.gson.*;
import org.javers.common.validation.Validate;
import org.javers.core.json.typeadapter.CdoSnapshotAdapter;
import org.javers.core.json.typeadapter.ChangeTypeAdapter;
import org.javers.core.json.typeadapter.GlobalCdoIdAdapter;
import org.javers.core.json.typeadapter.LocalDateTimeTypeAdapter;
import org.javers.core.json.typeadapter.LocalDateTypeAdapter;
import org.javers.core.json.typeadapter.ValueTypeAdapter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.GlobalIdFactory;

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
            new LocalDateTypeAdapter()
    };

    private boolean typeSafeValues = false;

    private final JsonConverter jsonConverter;

    /**
     * choose between new JsonConverterBuilder() or static jsonConverter()
     */
    public JsonConverterBuilder(GlobalIdFactory globalIdFactory) {
        jsonConverter = new JsonConverter();
        jsonConverter.registerJsonTypeAdapters(Arrays.asList(BUILT_IN_ADAPTERS));
        GlobalCdoIdAdapter globalCdoIdAdapter = registerGlobalCdoIdAdapter(globalIdFactory);
        registerChangeTypeAdapter(globalCdoIdAdapter);
        registerCdoSnapshotAdapter();
    }

    //TODO used only in test!
    public static JsonConverterBuilder jsonConverter(GlobalIdFactory globalIdFactory) {
        return new JsonConverterBuilder(globalIdFactory);
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
     * <p/>
     * Useful when serializing polymorfic collections like List or List&lt;Object&gt;
     *
     * @param typeSafeValues default false
     */
    public JsonConverterBuilder typeSafeValues(boolean typeSafeValues) {
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

    public JsonConverter build() {

        jsonConverter.registerJsonTypeAdapter(new ValueTypeAdapter(typeSafeValues));

        jsonConverter.initialize();
        return jsonConverter;
    }

    private GlobalCdoIdAdapter registerGlobalCdoIdAdapter(GlobalIdFactory globalIdFactory) {
        GlobalCdoIdAdapter globalCdoIdAdapter = new GlobalCdoIdAdapter(globalIdFactory);

        for (Type targetType : GlobalCdoIdAdapter.SUPPORTED) {
            jsonConverter.registerJsonTypeAdapter(targetType, globalCdoIdAdapter);
        }

        return globalCdoIdAdapter;
    }

    private void registerChangeTypeAdapter(GlobalCdoIdAdapter globalCdoIdAdapter) {
        ChangeTypeAdapter changeTypeAdapter = new ChangeTypeAdapter(globalCdoIdAdapter);

        for (Type targetType : ChangeTypeAdapter.SUPPORTED) {
            jsonConverter.registerJsonTypeAdapter(targetType, changeTypeAdapter);
        }
    }

    private void registerCdoSnapshotAdapter() {
        CdoSnapshotAdapter changeTypeAdapter = new CdoSnapshotAdapter();
        jsonConverter.registerJsonTypeAdapter(CdoSnapshot.class, changeTypeAdapter);
    }
}
