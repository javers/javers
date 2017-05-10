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

/**
 * @author bartosz walacik
 */
public class MapType extends KeyValueType {

    public MapType(Type baseJavaType) {
        super(baseJavaType, 2);
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        return mapStatic(sourceEnumerable, mapFunction, owner);
    }

    /**
     * @return immutable Map
     */
    public static Map mapStatic(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(mapFunction, owner);

        Map sourceMap = Maps.wrapNull(sourceEnumerable);
        Map targetMap = new HashMap(sourceMap.size());
        MapEnumerationOwnerContext enumeratorContext = new MapEnumerationOwnerContext(owner);

        mapEntrySet(sourceMap.entrySet(), mapFunction, enumeratorContext, (k,v) ->  targetMap.put(k,v));

        return Collections.unmodifiableMap(targetMap);
    }

    /**
     * Nulls keys are filtered
     */
    @Override
    public Object map(Object sourceEnumerable, Function mapFunction) {
        Validate.argumentsAreNotNull(mapFunction);

        Map sourceMap = Maps.wrapNull(sourceEnumerable);
        Map targetMap = new HashMap(sourceMap.size());

        mapEntrySetFilterNulls(sourceMap.entrySet(), mapFunction, (k,v) -> targetMap.put(k,v));

        return targetMap;
    }

    @Override
    public boolean isEmpty(Object map) {
        return map == null || ((Map)map).isEmpty();
    }

    public static void mapEntrySet(Collection<Map.Entry<?,?>> sourceEntries,
                              EnumerableFunction mapFunction,
                              MapEnumerationOwnerContext mapEnumerationContext,
                              BiConsumer entryConsumer) {
        for (Map.Entry entry : sourceEntries) {
            //key
            mapEnumerationContext.switchToKey();
            Object mappedKey = mapFunction.apply(entry.getKey(), mapEnumerationContext);

            //value
            mapEnumerationContext.switchToValue(mappedKey);
            Object mappedValue = mapFunction.apply(entry.getValue(), mapEnumerationContext);

            entryConsumer.accept(mappedKey, mappedValue);
        }
    }

    public static void mapEntrySetFilterNulls(Collection<Map.Entry<?,?>> sourceEntries,
                                              Function mapFunction,
                                              BiConsumer entryConsumer) {
        for (Map.Entry entry : sourceEntries) {
            Object mappedKey = mapFunction.apply(entry.getKey());
            if (mappedKey == null) continue;;

            Object mappedValue = mapFunction.apply(entry.getValue());

            entryConsumer.accept(mappedKey, mappedValue);
        }
    }
}
