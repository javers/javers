package org.javers.common.collections;

import java.util.ArrayList;
import java.util.List;

import static org.javers.common.validation.Validate.*;

public class Lists {

    public static <F, T> List<T> transform(List<F> input, Function<F, T> transformation) {
        argumentsAreNotNull(input, transformation);

        List<T> result = new ArrayList<>();
        for (F element : input) {
            result.add(transformation.apply(element));
        }
        return result;
    }
}
