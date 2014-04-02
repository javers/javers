package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Lists;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.validation.Validate;

import java.lang.reflect.Type;
import java.util.*;

import static org.javers.common.exception.exceptions.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
import static org.javers.common.exception.exceptions.JaversExceptionCode.NOT_IMPLEMENTED;

/**
 * Map where both keys and values
 * should be of {@link PrimitiveType} or {@link ValueType}.
 * <p/>
 *
 * Javers doesn't support complex maps with ValueObjects or Entities
 *
 * @author bartosz walacik
 */
public class MapType extends EnumerableType {
    private transient List<Class> elementTypes;

    public MapType(Type baseJavaType) {
        super(baseJavaType);

        if (getActualClassTypeArguments().size() == 2) {
            elementTypes = Lists.immutableListOf(getActualClassTypeArguments().get(0), getActualClassTypeArguments().get(1));
        } else {
            elementTypes = Collections.EMPTY_LIST;
        }
    }

    @Override
    public boolean isFullyParameterized() {
        return elementTypes.size() == 2;
    }

    @Override
    public Object map(Object sourceMap_, EnumerableFunction mapFunction) {
        Validate.argumentsAreNotNull(sourceMap_, mapFunction);
        Map<Object, Object> sourceMap = (Map) sourceMap_;
        Map<Object, Object> targetMap = new HashMap(sourceMap.size());

        for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
            Object mappedKey = mapFunction.apply(entry.getKey(), null);
            Object mappedValue = mapFunction.apply(entry.getValue(), mappedKey.toString());
            targetMap.put(mappedKey, mappedValue);
        }

        return targetMap;
    }

    /**
     * If both Key and Value type arguments are actual Classes,
     * returns List with key Class and value Class.
     * Otherwise returns empty List
     */
    @Override
    public List<Class> getElementTypes() {
        return elementTypes;
    }

    /**
     * never returns null
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public Class getKeyClass() {
        if (isFullyParameterized()) {
            return elementTypes.get(0);
        }
        throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED,getBaseJavaType().toString());
    }

    /**
     * never returns null
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public Class getValueClass() {
        if (isFullyParameterized()) {
            return elementTypes.get(1);
        }
        throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, getBaseJavaType().toString());
    }
}