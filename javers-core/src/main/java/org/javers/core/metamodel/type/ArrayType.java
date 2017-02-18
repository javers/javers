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
import java.util.Collection;
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
        Object targetArray = newPrimitiveOrObjectArray(getItemClass(), len);

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

    @Override
    public Object map(Object sourceContainer, Function mapFunction) {
        Validate.argumentsAreNotNull(sourceContainer, mapFunction);

        Collection sourceCol;
        if (sourceContainer.getClass().isArray()){
            sourceCol = Arrays.asList(sourceContainer);
        } else {
            sourceCol = (Collection) sourceContainer;
        }

        Object targetArray = Array.newInstance(getItemClass(), sourceCol.size());
        int i=0;
        for (Object sourceItem : sourceCol) {
            Array.set(targetArray, i++, mapFunction.apply(sourceItem));
        }

        return targetArray;
    }

    /**
     * Nulls are filtered
     */
    @Override
    public List mapToList(Object sourceArray, Function mapFunction) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction);

        int len = Array.getLength(sourceArray);
        List targetList = new ArrayList();

        for (int i=0; i<len; i++){
            Object sourceItem = Array.get(sourceArray,i);
            if (sourceItem == null) continue;
            targetList.add(mapFunction.apply(sourceItem));
        }

        return targetList;
    }

    Object newPrimitiveOrObjectArray(Class itemType, int len) {
        if (itemType.isPrimitive()){
            return Array.newInstance(itemType, len);
        } else {
            return new Object[len];
        }
    }
}
