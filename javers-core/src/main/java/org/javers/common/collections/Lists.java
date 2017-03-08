package org.javers.common.collections;

import java.util.*;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.unmodifiableList;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

public class Lists {

    public static List wrapNull(Object list){
        if (list == null){
            return Collections.emptyList();
        }
        return (List)list;
    }

    public static <T> List<T> immutableListOf(T... elements){
        return unmodifiableList(java.util.Arrays.asList(elements));
    }

    public static <E> List<E> asList(E... elements) {
        return (List)Arrays.asList(elements);
    }

    /**
     * @return index -> value
     */
    public static <T> Map<Integer, T> asMap(List<T> input) {
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

    /**
     * returns new list with elements from input that satisfies given filter condition
     */
    public static <T> List<T> positiveFilter(List<T> input, Predicate<T> filter) {
        argumentsAreNotNull(input, filter);
        return input.stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * returns new list with elements from input that don't satisfies given filter condition
     */
    public static <T> List<T> negativeFilter(List<T> input, final Predicate<T>  filter) {
        argumentsAreNotNull(input, filter);
        return input.stream().filter(element -> !filter.test(element)).collect(Collectors.toList());
    }

    public static <F, T> List<T> transform(Collection<F> input, Function<F, T> transformation) {
        argumentsAreNotNull(input, transformation);
        return input.stream().map(transformation::apply).collect(Collectors.toList());
    }

    public static <E> List<E> difference(List<E> first, List<E> second) {
        if (first == null) {
            return EMPTY_LIST;
        }

        if (second == null) {
            return first;
        }

        List<E> difference = new ArrayList<>(first);
        difference.removeAll(second);
        return difference;
    }

    public static <t> Collector<t, List<t>, List<t>> toImmutableList() {
        return Collector.of(ArrayList::new, List::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collections::unmodifiableList);
    }
}
