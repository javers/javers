package org.javers.common.collections;

import org.javers.common.reflection.ReflectionUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Currency;
import java.util.UUID;

/**
 * @author bartosz.walacik
 */
public class WellKnownValueTypes {
    private static final Class<?>[] VALUE_TYPES = {
            BigDecimal.class,
            BigInteger.class,
            ThreadLocal.class,
            UUID.class,
            Currency.class,
            URI.class,
            URL.class,
            CharSequence.class,
            String.class
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
