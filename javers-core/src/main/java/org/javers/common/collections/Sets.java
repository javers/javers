package org.javers.common.collections;

import java.util.*;

import org.javers.common.validation.Validate;

import static java.util.Collections.EMPTY_SET;
import static java.util.Arrays.asList;

/**
 * @author Maciej Zasada
 */
public class Sets {

    private Sets() {
    }

    /**
     * null args are allowed
     */
    public static <E> Set<E> intersection(Set<E> first, Set<E> second) {
        if (first == null || second == null) {
            return EMPTY_SET;
        }

        Set<E> intersection = new HashSet<>();

        for (E e : first) {
            if (second.contains(e)) {
                intersection.add(e);
            }
        }
        return intersection;
    }

    /**
     * null args are allowed
     */
    public static <E> Set<E> difference(Set<E> first, Set<E> second) {
        if (first == null) {
            return EMPTY_SET;
        }

        if (second == null) {
            return first;
        }

        Set<E> difference = new HashSet<>(first);
        difference.removeAll(second);
        return difference;
    }

    public static <E> Set<E> asSet(E... elements) {
        return asSet(asList(elements));
    }

    public static <E> Set<E> asSet(Collection<E> elements) {
        return new HashSet<>(elements);
    }

    public static <F, T> Set<T> transform(Set<F> input, Function<F, T> transformation) {
        Validate.argumentIsNotNull(input);
        Validate.argumentIsNotNull(transformation);

        Set<T> result = new HashSet<>();
        for (F element : input) {
            result.add(transformation.apply(element));
        }
        return result;
    }

    private static <E> Set<E> nullSafe(Set<E> set) {
        if (set == null) {
            return EMPTY_SET;
        }
        return set;
    }
}
