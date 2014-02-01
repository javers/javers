package org.javers.common.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class Arrays {

    /**
     * returns new list with elements from array
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static List<Object> asList(Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException(array.getClass().getSimpleName() + "is not array");
        }

        List<Object> list = new ArrayList<>();

        for (int i=0 ;i< Array.getLength(array); i++) {
            list.add(Array.get(array, i));
        }

        return list;
    }
}
