package org.javers.common.collections;

import org.javers.common.validation.Validate;

import java.util.ArrayList;
import java.util.List;

public class Lists {

    public static <F, T> List<T> transform(List<F> input, Function<F, T> transformation) {
        Validate.argumentIsNotNull(input);
        Validate.argumentIsNotNull(transformation);

        List<T> result = new ArrayList<>();
        for (F element : input) {
            result.add(transformation.apply(element));
        }
        return result;
    }
}
