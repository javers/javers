package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class ArrayType extends ContainerType {
    private transient List<Class> elementTypes;

    public ArrayType(Type baseJavaType) {
        super(baseJavaType);
        elementTypes = Lists.immutableListOf(getBaseJavaClass().getComponentType());

    }

    @Override
    public boolean isFullyParameterized() {
        return true;
    }

    @Override
    public boolean isAssignableFrom(Class givenType) {
        return givenType.isArray();
    }

    @Override
    public List<Class> getElementTypes() {
        return elementTypes;
    }

    @Override
    public Object map(Object sourceArray, EnumerableFunction mapFunction) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction);

        int len = Array.getLength(sourceArray);
        Object targetArray = new Object[len];

        for (int i=0; i<len; i++){
            Object sourceVal = Array.get(sourceArray,i);

            Array.set(targetArray, i, mapFunction.apply(sourceVal,""+i));
        }
        return targetArray;
    }
}
