package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;


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

        Object targetArray;
        if (getItemClass().isPrimitive()){
            targetArray = Array.newInstance(getItemClass(), len);
        } else {
            targetArray = new Object[len];
        }

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
}
