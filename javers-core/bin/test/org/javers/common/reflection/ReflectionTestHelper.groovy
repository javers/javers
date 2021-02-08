package org.javers.common.reflection

import java.lang.reflect.Field

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
class ReflectionTestHelper {

    static Field getFieldFromClass(Class targetClass, String fieldName) {
        try {
            return targetClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
