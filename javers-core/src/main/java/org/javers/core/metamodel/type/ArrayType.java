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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.javers.common.collections.Collections.wrapNull;

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

    @Override
    public Object map(Object sourceArray, Function mapFunction) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction);

        int len = Array.getLength(sourceArray);
        Object targetArray = newPrimitiveOrObjectArray(len);

        for (int i=0; i<len; i++){
            Object sourceVal = Array.get(sourceArray,i);
            Array.set(targetArray, i, mapFunction.apply(sourceVal));
        }
        return targetArray;
    }

    @Override
    protected Stream<Object> items(Object source) {
        if (source == null || Array.getLength(source) == 0) {
            return Stream.empty();
        }

        return Arrays.asList((Object[])source).stream();
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

    @Override
    public Object empty() {
        return Collections.emptyList().toArray();
    }
}
