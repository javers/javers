package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class ArrayType extends ContainerType {
    private final transient Class elementType;

    public ArrayType(Type baseJavaType) {
        super(baseJavaType);
        elementType = getBaseJavaClass().getComponentType();

    }

    @Override
    public boolean isAssignableFrom(Class givenType) {
        return givenType.isArray();
    }

    @Override
    public Class getElementType() {
        return elementType;
    }

    @Override
    public Object map(Object sourceArray, EnumerableFunction mapFunction) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction);

        int len = Array.getLength(sourceArray);
        Object targetArray = new Object[len];

        for (int i=0; i<len; i++){
            Object sourceVal = Array.get(sourceArray,i);

            Array.set(targetArray, i, mapFunction.apply(sourceVal,i));
        }
        return targetArray;
    }
}
