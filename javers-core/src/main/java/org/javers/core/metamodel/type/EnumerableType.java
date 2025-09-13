package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import org.javers.common.exception.JaversException;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

/**
 * Collection or Array or Map
 * @author bartosz walacik
 */
public abstract class EnumerableType extends ClassType {
    private final TypeMapperLazy typeMapperLazy;
    private final List<Type> concreteTypeArguments;

    EnumerableType(Type baseJavaType, int expectedArgs, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, Optional.empty(), expectedArgs);
        this.typeMapperLazy = typeMapperLazy;
        this.concreteTypeArguments = buildEnumerableConcreteTypeArguments(expectedArgs).map(Collections::unmodifiableList).orElse(null);
    }

    private Optional<List<Type>> buildEnumerableConcreteTypeArguments(int expectedArgs) {
        if (isNonDefaultEnumerableTypeParameters(super.getConcreteClassTypeArguments(), expectedArgs))
            return Optional.empty(); // baseJavaType has the needed type parameters, delegate logic to base class

        final Class<?> enumerableClass = ((Supplier<Class<?>>) () -> {
            try {
                return getEnumerableInterface();
            } catch (JaversException ex) {
                return null;
            }
        }).get();
        if (enumerableClass == null)
            return Optional.empty(); // Some enumerable types (e.g. Array) don't have a common interface

        final Class<?> baseJavaClass = getBaseJavaClass();
        if (baseJavaClass == enumerableClass)
            return Optional.empty(); // No need to traverse inheritance tree, delegate logic to base class

        for (Class<?> current = baseJavaClass; current != null && enumerableClass.isAssignableFrom(current); current = current.getSuperclass()) {
            final Type superClass = current.getGenericSuperclass();
            if (superClass instanceof ParameterizedType) {
                final Type rawType = ((ParameterizedType) superClass).getRawType();
                if (rawType instanceof Class && enumerableClass.isAssignableFrom((Class<?>) rawType)) {
                    final List<Type> typeParameters = buildListOfConcreteTypeArguments(superClass, expectedArgs);
                    if (isNonDefaultEnumerableTypeParameters(typeParameters, expectedArgs))
                        return Optional.of(typeParameters);
                }
            }

            final ArrayList<Type> interfaces = new ArrayList<>(Arrays.asList(current.getGenericInterfaces()));
            for (int i = 0; i < interfaces.size(); ++i) {
                final Type curInterface = interfaces.get(i);

                final List<Type> superInterfaces = Arrays.asList(ReflectionUtil.extractClass(curInterface).getGenericInterfaces());
                interfaces.addAll(superInterfaces);

                if (!(curInterface instanceof ParameterizedType))
                    continue;

                final Type rawType = ((ParameterizedType) curInterface).getRawType();
                if (!(rawType instanceof Class && enumerableClass.isAssignableFrom((Class<?>) rawType)))
                    continue;

                final List<Type> typeParameters = buildListOfConcreteTypeArguments(curInterface, expectedArgs);
                if (isNonDefaultEnumerableTypeParameters(typeParameters, expectedArgs))
                    return Optional.of(typeParameters);
            }
        }

        return Optional.empty();
    }

    static private boolean isNonDefaultEnumerableTypeParameters(List<Type> typeParameters, int expectedArgs) {
        return typeParameters != null && typeParameters.size() == expectedArgs && typeParameters.stream().anyMatch(it -> it != DEFAULT_TYPE_PARAMETER);
    }

    @Override
    public List<Type> getConcreteClassTypeArguments() {
        if (concreteTypeArguments == null)
            return super.getConcreteClassTypeArguments();
        return concreteTypeArguments;
    }

    @Override
    protected Object[] spawnConstructorArgs(Type baseJavaType) {
        return new Object[]{baseJavaType, getTypeMapperLazy()};
    }

    protected Class[] spawnConstructorArgTypes() {
        return new Class[]{Type.class, TypeMapperLazy.class};
    }

    protected TypeMapperLazy getTypeMapperLazy() {
        return typeMapperLazy;
    }

    /**
     * OwnerContext aware version of {@link #map(Object, EnumerableFunction, OwnerContext)}
     *
     * @return immutable Enumerable
     */
    public abstract Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner);

    public abstract Class<?> getEnumerableInterface();

    /**
     * Returns a new Enumerable (unmodifiable when possible)
     * with items from sourceEnumerable mapped by mapFunction.
     */
    public Object map(Object sourceEnumerable, Function mapFunction) {
        return map(sourceEnumerable, mapFunction, false);
    }

    /**
     * For building Shadows. Item type of new enumerable matters only for arrays
     */
    public Object mapPreservingSourceItemType(Object sourceEnumerable, Function mapFunction) {
        return this.map(sourceEnumerable, mapFunction);
    }

    public abstract Object map(Object sourceEnumerable, Function mapFunction, boolean filterNulls);

    public abstract boolean isEmpty(Object container);

    public abstract Object empty();

    /**
     * Returns a new, unmodifiable Enumerable with filtered items,
     * nulls are omitted.
     */
    public <T> List<T> filterToList(Object source, Class<T> filter) {
        Validate.argumentsAreNotNull(filter);

        return (List) unmodifiableList(
        items(source).filter(item -> item!=null && filter.isAssignableFrom(item.getClass()))
                     .collect(Collectors.toList()));
    }

    protected abstract Stream<Object> items(Object source);
}
