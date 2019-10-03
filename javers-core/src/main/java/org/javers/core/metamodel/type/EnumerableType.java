package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;

import java.util.*;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

/**
 * Collection or Array or Map
 * @author bartosz walacik
 */
public abstract class EnumerableType extends ClassType {

    EnumerableType(Type baseJavaType, int expectedArgs) {
        super(baseJavaType, Optional.empty(), expectedArgs);
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
