package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.reflection.ReflectionUtil;

import java.lang.reflect.Type;

import static org.javers.common.exception.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;

/**
 * Collection or Array
 *
 * @author bartosz walacik
 */
public abstract class ContainerType extends EnumerableType {

    ContainerType(Type baseJavaType) {
        super(baseJavaType);
    }

    /**
     * never returns null
     *
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public Type getItemType(){
        if (isFullyParametrized()) {
            return getActualTypeArguments().get(0);
        }
        throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, getBaseJavaType().toString());
    }

    /**
     * never returns null
     *
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public Class getItemClass(){
        return ReflectionUtil.extractClass(getBaseJavaType());
    }
}
