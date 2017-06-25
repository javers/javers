package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author bartosz walacik
 */
public class ArrayType extends ContainerType {

    public ArrayType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public List<Type> getConcreteClassTypeArguments() {
        return (List)Lists.immutableListOf( getBaseJavaClass().getComponentType() );
    }

    @Override
    public Object map(Object sourceArray, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction, owner);

        int len = Array.getLength(sourceArray);
        Object targetArray = newPrimitiveOrObjectArray(len);

        EnumerationAwareOwnerContext enumerationContext = new IndexableEnumerationOwnerContext(owner);
        for (int i=0; i<len; i++){
            Object sourceVal = Array.get(sourceArray,i);
            Array.set(targetArray, i, mapFunction.apply(sourceVal, enumerationContext));
        }
        return targetArray;
    }

    @Override
    public boolean isEmpty(Object array) {
        return array == null ||  Array.getLength(array) == 0;
    }

    /**
     * Nulls are filtered
     */
    @Override
    public Object map(Object sourceArray, Function mapFunction) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction);

        int len = Array.getLength(sourceArray);
        if (len == 0) {
            return sourceArray;
        }

        List targetList = new ArrayList();
        for (int i=0; i<len; i++){
            Object sourceItem = Array.get(sourceArray,i);
            if (sourceItem == null) continue;
            targetList.add(mapFunction.apply(sourceItem));
        }

        Object targetArray = newItemTypeOrObjectArray(targetList.get(0), targetList.size());
        int i=0;
        for (Object targetItem : targetList) {
            Array.set(targetArray, i++, targetItem);
        }

        return targetArray;
    }

    private Object newItemTypeOrObjectArray(Object sample, int len) {
        if (getItemClass().isAssignableFrom(sample.getClass())) {
            return Array.newInstance(getItemClass(), len);

        }
        return new Object[len];
    }

    private Object newPrimitiveOrObjectArray(int len) {
        if (getItemClass().isPrimitive()){
            return Array.newInstance(getItemClass(), len);
        }
        return new Object[len];
    }

    @Override
    public boolean equals(Object left, Object right) {
        //see https://github.com/javers/javers/issues/546
        return Arrays.equals((Object[]) left, (Object[]) right);
    }
}
