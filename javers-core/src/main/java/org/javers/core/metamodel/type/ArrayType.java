package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
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

        Object targetArray = newArray(sourceArray, null, false);

        int len = Array.getLength(sourceArray);
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
    public Object map(Object sourceArray, Function mapFunction, boolean filterNulls) {
        Validate.argumentsAreNotNull(sourceArray, mapFunction);

        Object targetArray = newArray(sourceArray, mapFunction, true);

        int len = Array.getLength(sourceArray);
        int t = 0;
        for (int i=0; i<len; i++) {
            Object sourceVal = Array.get(sourceArray,i);

            Object mappedVal = mapFunction.apply(sourceVal);
            if (mappedVal == null && filterNulls) continue;
            Array.set(targetArray, t++, mappedVal);
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

    private Object newArray(Object sourceArray, Function mapFunction, boolean doSample) {
        int len = Array.getLength(sourceArray);
        if (len == 0) {
            return sourceArray;
        }

        if (getItemClass().isPrimitive()){
            return Array.newInstance(getItemClass(), len);
        }

        if (doSample) {
            Object sample = mapFunction.apply(Array.get(sourceArray, 0));
            if (getItemClass().isAssignableFrom(sample.getClass())) {
                return Array.newInstance(getItemClass(), len);
            }
        }

        return Array.newInstance(Object.class, len);
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

    @Override
    public Class<?> getEnumerableInterface() {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }
}
