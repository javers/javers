package org.javers.common.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.javers.common.validation.Validate;

/**
 * @author Maciej Zasada
 */
public class Sets {

    private Sets() {
    }

    public static <E> Set<E> difference(Set<E> first, Set<E> second) {
        Validate.argumentIsNotNull(first);
        Validate.argumentIsNotNull(second);

        Set<E> difference = new HashSet<>(first);
        difference.removeAll(second);
        return difference;
    }

    public static <E> Set<E> asSet(E... elements) {
        return asSet(Arrays.asList(elements));
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
}
