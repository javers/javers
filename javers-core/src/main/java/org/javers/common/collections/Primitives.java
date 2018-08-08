package org.javers.common.collections;

import org.javers.common.reflection.ReflectionUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author bartosz walacik
 */
public class Primitives {
    private static final List<Class<?>> PRIMITIVE_NUMBER_TYPES = Lists.asList(
            int.class, long.class, short.class,
            float.class, double.class, byte.class
    );

    private static final List<Class<?>> PRIMITIVE_TYPES = Lists.asList(
            boolean.class, char.class, Integer.class, Long.class,
            Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class);

    private static final List<Class<?>> JSON_BASIC_TYPES = Lists.asList(
                                                         int.class, Integer.class,
                                                         double.class, Double.class,
                                                         boolean.class, Boolean.class,
                                                         String.class );

    public static List<Class<?>> getPrimitiveAndBoxTypes() {
        return java.util.Collections.unmodifiableList(
                Stream.concat(PRIMITIVE_NUMBER_TYPES.stream(),
                               PRIMITIVE_TYPES.stream()).collect(Collectors.toList()));
    }

    public static boolean isJsonBasicType(Object object) {
        if (object == null) {
            return false;
        }

        return  isJsonBasicType(object.getClass());
    }

    public static boolean isPrimitiveNumber(Class<?> clazz) {
        return PRIMITIVE_NUMBER_TYPES.contains(clazz);
    }

    public static boolean isPrimitiveOrBox(Object object) {
        if (object == null) {
            return false;
        }

        return  isPrimitiveOrBox(object.getClass());
    }

    public static boolean isJsonBasicType(Class clazz) {
        return ReflectionUtil.isAssignableFromAny(clazz, JSON_BASIC_TYPES);
    }

    private static boolean isPrimitiveOrBox(Class clazz) {
        return ReflectionUtil.isAssignableFromAny(clazz, PRIMITIVE_TYPES);
    }
}
