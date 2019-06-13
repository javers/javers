package org.javers.common.collections;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.MissingProperty;

import java.util.*;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_SET;

/**
 * @author Maciej Zasada
 */
public class Sets {

    private Sets() {
    }


    public static Set wrapNull(Object set) {
        if (set == null || set == MissingProperty.INSTANCE) {
            return Collections.emptySet();
        }
        return (Set) set;
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

    public static <E> Set<E> xor(Set<E> first, Set<E> second) {
        Set<E> xor = difference(first, second);
        xor.addAll(difference(second, first));
        return xor;
    }

    /**
     * null args are allowed
     *
     * @return mutable set
     */
    public static <E> Set<E> difference(Set<E> first, Set<E> second) {
        if (first == null || first.size() == 0) {
            return new HashSet<>();
        }

        if (second == null || second.size() == 0) {
            return first;
        }

        Set<E> difference = new HashSet<>(first);
        difference.removeAll(second);
        return difference;
    }

    /**
     * null args are allowed
     */
    public static <E> Collection<E> difference(Set<E> first, Set<E> second, Function<E, Integer> hasher) {
        if (first == null || first.size() == 0) {
            return Collections.emptyList();
        }

        if (second == null || second.size() == 0) {
            return first;
        }

        Map<Integer, E> map = new HashMap();

        first.stream().forEach(e -> map.put(hasher.apply(e), e));
        second.stream().forEach(e -> map.remove(hasher.apply(e)));

        return Collections.unmodifiableCollection(map.values());
    }

    public static <E> Set<E> asSet(E... elements) {
        return asSet(asList(elements));
    }

    public static <E> Set<E> asSet(Collection<E> elements) {
        if (elements == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<>(elements));
    }

    /**
     * @return ImmutableSet
     */
    public static <F, T> Set<T> transform(Set<F> input, Function<F, T> transformation) {
        Validate.argumentsAreNotNull(input, transformation);
        return input.stream().map(transformation::apply).collect(toImmutableSet());
    }

    private static <E> Set<E> nullSafe(Set<E> set) {
        if (set == null) {
            return EMPTY_SET;
        }
        return set;
    }

    /**
     * @return index -> value
     */
    public static <T> Map<Integer, T> asMap(Set<T> input) {
        if (input == null){
            return null;
        }

        Map<Integer, T> result = new HashMap<>();
        int i = 0;

        for (T element : input) {
            result.put(i, element);
            i++;
        }

        return result;
    }

    public static <t> Collector<t, Set<t>, Set<t>> toImmutableSet() {
        return Collector.of(HashSet::new, Set::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collections::unmodifiableSet);
    }
}
