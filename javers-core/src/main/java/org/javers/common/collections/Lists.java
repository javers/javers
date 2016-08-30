package org.javers.common.collections;

import java.util.*;
import java.util.Collections;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.unmodifiableList;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

public class Lists {

    public static List wrapNull(List list){
        if (list == null){
            return Collections.emptyList();
        }
        return list;
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

        List<T> result = new ArrayList<>();
        for (T element : input) {
            if (filter.apply(element)){
                result.add(element);
            }
        }
        return result;
    }

    /**
     * returns new list with elements from input that don't satisfies given filter condition
     */
    public static <T> List<T> negativeFilter(List<T> input, final Predicate<T>  filter) {
        argumentsAreNotNull(input, filter);
        return positiveFilter(input, negative(filter));
    }

    public static <T> Predicate<T> negative(final Predicate<T> predicate) {
        return new Predicate<T>() {
            public boolean apply(T input) {
                return !predicate.apply(input);
            }
        };
    }

    public static <F, T> List<T> transform(List<F> input, Function<F, T> transformation) {
        argumentsAreNotNull(input, transformation);

        List<T> result = new ArrayList<>();
        for (F element : input) {
            result.add(transformation.apply(element));
        }
        return Collections.unmodifiableList(result);
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

    public static <T> String asString(List<T> list) {
        if (list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (T s : list) {
            sb.append(s);
            sb.append(", ");
        }

        return sb.substring(0, sb.length() - 2);
    }
}
