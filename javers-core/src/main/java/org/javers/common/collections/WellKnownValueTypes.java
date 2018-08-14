package org.javers.common.collections;

import org.javers.common.reflection.ReflectionUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * @author bartosz.walacik
 */
public class WellKnownValueTypes {
    private static final List<Class<?>> VALUE_TYPES = Lists.asList(
            BigDecimal.class,
            BigInteger.class,
            ThreadLocal.class,
            UUID.class,
            Currency.class,
            URI.class,
            URL.class,
            CharSequence.class,
            String.class,
            Number.class
    );

    public static List<Class<?>> getValueTypes() {
        return java.util.Collections.unmodifiableList(VALUE_TYPES);
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
