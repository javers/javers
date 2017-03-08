package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author bartosz walacik
 */
public class MapType extends KeyValueType {

    public MapType(Type baseJavaType) {
        super(baseJavaType, 2);
    }

    @Override
    public Object map(Object sourceMap_, EnumerableFunction mapFunction, OwnerContext owner) {
        return mapStatic(sourceMap_, mapFunction, owner);
    }

    public static Map mapStatic(Object sourceMap_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);

        if (sourceMap_ == null) {
            return Collections.emptyMap();
        }

        Map<Object, Object> sourceMap = (Map) sourceMap_;
        Map<Object, Object> targetMap = new HashMap(sourceMap.size());

        MapEnumerationOwnerContext mapEnumerationContext = new MapEnumerationOwnerContext(owner);

        for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
            //key
            mapEnumerationContext.switchToKey();
            Object mappedKey = mapFunction.apply(entry.getKey(), mapEnumerationContext);

            //value
            mapEnumerationContext.switchToValue(mappedKey);
            Object mappedValue = mapFunction.apply(entry.getValue(), mapEnumerationContext);

            targetMap.put(mappedKey, mappedValue);
        }

        return Collections.unmodifiableMap(targetMap);
    }

    @Override
    public boolean isEmpty(Object map) {
        return map == null || ((Map)map).isEmpty();
    }

    @Override
    public Object map(Object sourceEnumerable, Function mapFunction) {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }
}
