package org.javers.common.collections;

import org.javers.common.validation.Validate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.javers.common.collections.Arrays.asList;

/**
 * @author pawel szymczyk
 */
public class Collections {

    public static <F, T> Collection<T> transform(Collection<F> input, Function<F, T> transformation) {
        Validate.argumentsAreNotNull(input, transformation);

        if (input instanceof Set) {
            return Sets.transform((Set<F>) input, transformation);
        } else if (input instanceof List) {
            return Lists.transform((List<F>) input, transformation);
        } else {
            throw new IllegalArgumentException("At this moment Javers don't support " + input.getClass().getSimpleName());
        }
    }

    public static <E> Collection<E> difference(Collection<E> first, Collection<E> second) {
        if (first instanceof List) {
            return Lists.difference((List) first,(List) second);
        } else if (first instanceof Set) {
            return Sets.difference((Set) first,(Set) second);
        } else {
            throw new IllegalArgumentException("At this moment Javers don't support "  + first.getClass().getSimpleName());
        }
    }

    public static Collection<Object> asCollection(Object arrayOrCollection) {
        Validate.argumentIsNotNull(arrayOrCollection);

        if (arrayOrCollection.getClass().isArray()) {
            return asList(arrayOrCollection);
        } else if (arrayOrCollection instanceof  Collection) {
            return (Collection<Object>) arrayOrCollection;
        } else {
            throw new IllegalArgumentException("expected Array or Collection, got "+arrayOrCollection.getClass());
        }
    }
}
