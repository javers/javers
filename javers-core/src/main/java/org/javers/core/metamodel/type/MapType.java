package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Maps;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author bartosz walacik
 */
public class MapType extends KeyValueType {

    public MapType(Type baseJavaType, TypeMapperLazy typeMapperlazy) {
        super(baseJavaType, 2, typeMapperlazy);
    }

    /**
     * @return immutable Map
     */
    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(mapFunction, owner);

        Map sourceMap = Maps.wrapNull(sourceEnumerable);
        Map targetMap = new HashMap(sourceMap.size());
        MapEnumerationOwnerContext enumeratorContext = new MapEnumerationOwnerContext(this, owner);

        mapEntrySet(this, sourceMap.entrySet(), mapFunction, enumeratorContext, (k,v) ->  targetMap.put(k,v), false);

        return Collections.unmodifiableMap(targetMap);
    }

    @Override
    public Object map(Object source, Function mapFunction, boolean filterNulls) {
        Validate.argumentsAreNotNull(mapFunction);

        Map sourceMap = Maps.wrapNull(source);
        Map targetMap = new HashMap(sourceMap.size());

        mapEntrySet(this, sourceMap.entrySet(), mapFunction, (k,v) -> targetMap.put(k,v), filterNulls);

        return Collections.unmodifiableMap(targetMap);
    }

    @Override
    public boolean isEmpty(Object map) {
        return map == null || ((Map)map).isEmpty();
    }

    public static void mapEntrySet(KeyValueType keyValueType,
                              Collection<Map.Entry<?,?>> sourceEntries,
                              EnumerableFunction mapFunction,
                              MapEnumerationOwnerContext mapEnumerationContext,
                              BiConsumer entryConsumer,
                              boolean filterNulls) {
        for (Map.Entry entry : sourceEntries) {
            //key
            mapEnumerationContext.switchToKey();
            Object mappedKey = mapFunction.apply(entry.getKey(), mapEnumerationContext);
            if (mappedKey == null && filterNulls) continue;

            //value
            mapEnumerationContext.switchToValue(mappedKey);

            Object entryValue = entry.getValue();
            Object mappedValue = null;
            if (entryValue != null) {
                if (keyValueType.getValueJaversType() instanceof ContainerType) {
                    ContainerType containerType = (ContainerType) keyValueType.getValueJaversType();
                    mappedValue = containerType.map(entryValue, mapFunction, mapEnumerationContext);
                } else {
                    mappedValue = mapFunction.apply(entryValue, mapEnumerationContext);
                }
            }

            entryConsumer.accept(mappedKey, mappedValue);
        }
    }

    public static void mapEntrySet(KeyValueType keyValueType,
                                   Collection<Map.Entry<?,?>> sourceEntries,
                                   Function mapFunction,
                                   BiConsumer entryConsumer,
                                   boolean filterNulls) {
        MapEnumerationOwnerContext enumeratorContext = MapEnumerationOwnerContext.dummy(keyValueType);
        EnumerableFunction enumerableFunction = (input, ownerContext) -> mapFunction.apply(input);
        mapEntrySet(keyValueType, sourceEntries, enumerableFunction, enumeratorContext, entryConsumer,  filterNulls);
    }

    @Override
    public Object empty() {
        return Collections.emptyMap();
    }

    @Override
    protected Stream<Map.Entry> entries(Object source) {
        Map sourceMap = Maps.wrapNull(source);
        return sourceMap.entrySet().stream();
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return Map.class;
    }
}
