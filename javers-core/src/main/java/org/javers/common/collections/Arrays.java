package org.javers.common.collections;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.argumentCheck;

/**
 * @author pawel szymczyk
 */
public class Arrays {
    public static Class INT_ARRAY_TYPE = new int[]{}.getClass();
    public static Class INTEGER_ARRAY_TYPE = new Integer[]{}.getClass();
    public static Class OBJECT_ARRAY_TYPE = new Object[]{}.getClass();

    /**
     * @return index -> value
     */
    public static <T> Map<Integer, T> asMap(Object array) {
        Map<Integer, T> result = new HashMap<>();
        if (array == null){
            return result;
        }

        for (int i=0 ;i< Array.getLength(array); i++) {
            result.put(i, (T) Array.get(array, i));
        }

        return result;
    }
    /**
     * @return new list with elements from array
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

    public static int[] intArray(int... data){
       int[] ret = new int[data.length];
       for (int i=0; i<data.length; i++) {
           ret[i] = data[i];
       }
       return ret;
    }

    /**
     * Unfortunately, Java forces us to write such complex code
     * just to compare two arrays ...
     */
    public static boolean equals(Object arr1, Object arr2) {
        Class<?> c = arr1.getClass();
        argumentCheck(c.isArray(), arr1 + " is not an Array");

        if (!c.getComponentType().isPrimitive()) {
            c = Object[].class;
        }

        try {
            Method eqMethod = java.util.Arrays.class.getMethod("equals", c, c);
            return (Boolean) eqMethod.invoke(null, arr1, arr2);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int length(Object arr) {
        if (arr == null) {
            return 0;
        }

        Class<?> c = arr.getClass();
        argumentCheck(c.isArray(), arr + " is not an Array");

        if (arr instanceof byte[]) {
            return ((byte[]) arr).length;
        }
        if (arr instanceof char[]) {
            return ((char[]) arr).length;
        }
        if (arr instanceof short[]) {
            return ((short[]) arr).length;
        }
        if (arr instanceof int[]) {
            return ((int[]) arr).length;
        }
        if (arr instanceof long[]) {
            return ((long[]) arr).length;
        }
        if (arr instanceof boolean[]) {
            return ((boolean[]) arr).length;
        }
        if (arr instanceof float[]) {
            return ((float[]) arr).length;
        }
        if (arr instanceof double[]) {
            return ((double[]) arr).length;
        }
        return ((Object[]) arr).length;
    }
}
