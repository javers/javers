package org.javers.common.collections;

import org.javers.common.reflection.ReflectionUtil;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * @author bartosz.walacik
 */
public class WellKnownValueTypes {
    private static final Class<?>[] VALUE_TYPES = {
            LocalDateTime.class,
            LocalDate.class,
            BigDecimal.class,
            Date.class,
            ThreadLocal.class,
            UUID.class,
    };

    public static Class<?>[] getValueTypes() {
        return VALUE_TYPES;
    }

    public static boolean isValueType(Object object) {
        if (object == null) {
            return false;
        }
        return  isValueType(object.getClass());
    }

    private static boolean isValueType(Class clazz) {
        return ReflectionUtil.isAssignableFromAny(clazz, VALUE_TYPES);
    }
}
