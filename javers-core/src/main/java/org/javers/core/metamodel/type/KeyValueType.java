package org.javers.core.metamodel.type;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author bartosz.walacik
 */
public abstract class KeyValueType extends EnumerableType {

    public KeyValueType(Type baseJavaType, int expectedArgs, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, expectedArgs, typeMapperLazy);
    }

    /**
     * never returns null
     */
    public Type getKeyJavaType() {
        return getConcreteClassTypeArguments().get(0);
    }

    /**
     * never returns null
     */
    public Type getValueJavaType() {
        return getConcreteClassTypeArguments().get(1);
    }

    @Override
    public <T> List<T> filterToList(Object source, Class<T> filter) {
        return super.filterToList(source, filter);
    }

    @Override
    protected Stream<Object> items(Object source) {
        return entries(source).flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()));
    }

    protected abstract Stream<Map.Entry> entries(Object source);

    public JaversType getValueJaversType() {
        return getTypeMapperLazy().getJaversType(getValueJavaType());
    }

    public JaversType getKeyJaversType() {
        return getTypeMapperLazy().getJaversType(getKeyJavaType());
    }
}
