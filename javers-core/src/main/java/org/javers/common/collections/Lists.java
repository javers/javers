package org.javers.common.collections;

import java.util.ArrayList;
import java.util.List;

import static org.javers.common.validation.Validate.*;

public class Lists {

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
        return result;
    }
}
