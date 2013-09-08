package org.javers.common.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ReflectionTestHelper {

    public static Field getFieldFromClass(Class targetClass, String fieldName) {
        try {
            return targetClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMethodFromClass(Class targetClass, String methodName) {
        try {
            return targetClass.getDeclaredMethod(methodName, new Class<?>[]{});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
