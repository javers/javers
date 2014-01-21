package org.javers.common.collections;

/**
 * @author bartosz walacik
 */
public class Primitives {
    private static final Class<?>[] PRIMITIVE_TYPES = { int.class, long.class, short.class,
            float.class, double.class, byte.class, boolean.class, char.class, Integer.class, Long.class,
            Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class };

    private static final Class<?>[] JSON_BASIC_TYPES = { int.class, Integer.class,
                                                         double.class, Double.class,
                                                         boolean.class, Boolean.class,
                                                         String.class };

    public static Class<?>[] getPrimitiveAndBoxTypes() {
        return PRIMITIVE_TYPES;
    }

    public static boolean isJsonBasicType(Object object) {
        if (object == null) {
            return false;
        }

        return  isJsonBasicType(object.getClass());
    }

    public static boolean isPrimitiveOrBox(Object object) {
        if (object == null) {
            return false;
        }

        return  isPrimitiveOrBox(object.getClass());
    }

    public static boolean isJsonBasicType(Class clazz) {
        return isAssignableFromAny(clazz, JSON_BASIC_TYPES);
    }

    public static boolean isPrimitiveOrBox(Class clazz) {
        return isAssignableFromAny(clazz, PRIMITIVE_TYPES);
    }

    private static boolean isAssignableFromAny(Class clazz, Class<?>[] assignableFrom) {
        for (Class<?> standardPrimitive : assignableFrom) {
            if (standardPrimitive.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
}
