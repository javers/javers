package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.exceptions.JaversException;

import java.lang.reflect.Type;

import static org.javers.common.exception.exceptions.JaversExceptionCode.NOT_IMPLEMENTED;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {
    private transient Class elementType;

    public CollectionType(Type baseJavaType) {
        super(baseJavaType);
        if (getActualClassTypeArguments().size() == 1) {
            elementType =  getActualClassTypeArguments().get(0);
        }
    }

    @Override
    public Class getElementType() {
        return elementType;
    }

    /**
     * implemented in subclasses
     */
    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction) {
        throw new JaversException(NOT_IMPLEMENTED);
    }
}
