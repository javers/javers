package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.javers.common.exception.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;

/**
 * @author bartosz walacik
 */
public class MapType extends EnumerableType {

    public MapType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public boolean isFullyParametrized() {
        return getActualTypeArguments().size() == 2;
    }

    @Override
    public Map map(Object sourceMap_, EnumerableFunction mapFunction, OwnerContext owner) {
        return mapStatic(sourceMap_, mapFunction, owner);
    }

    public static Map mapStatic(Object sourceMap_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);

        if (sourceMap_ == null) {
            return Collections.EMPTY_MAP;
        }

        Map<Object, Object> sourceMap = (Map) sourceMap_;
        Map<Object, Object> targetMap = new HashMap(sourceMap.size());

        MapEnumeratorContext enumeratorContext = new MapEnumeratorContext();
        owner.setEnumeratorContext(enumeratorContext);

        for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
            //key
            enumeratorContext.switchToKey();
            Object mappedKey = mapFunction.apply(entry.getKey(), owner);

            //value
            enumeratorContext.switchToValue(mappedKey);
            Object mappedValue = mapFunction.apply(entry.getValue(), owner);

            targetMap.put(mappedKey, mappedValue);
        }

        return Collections.unmodifiableMap(targetMap);
    }

    @Override
    public boolean isEmpty(Object map) {
        return map == null || ((Map)map).isEmpty();
    }

    /**
     * never returns null
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public Type getKeyType() {
        if (isFullyParametrized()) {
            return getActualTypeArguments().get(0);
        }
        throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, getBaseJavaType().toString());
    }

    /**
     * never returns null
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public Type getValueType() {
        if (isFullyParametrized()) {
            return getActualTypeArguments().get(1);
        }
        throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, getBaseJavaType().toString());
    }

}