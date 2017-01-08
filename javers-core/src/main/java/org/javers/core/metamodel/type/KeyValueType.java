package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

/**
 * @author bartosz.walacik
 */
public abstract class KeyValueType extends EnumerableType {

    public KeyValueType(Type baseJavaType, int expectedArgs) {
        super(baseJavaType, expectedArgs);
    }

    /**
     * never returns null
     */
    public Type getKeyType() {
        return getConcreteClassTypeArguments().get(0);
    }

    /**
     * never returns null
     */
    public Type getValueType() {
        return getConcreteClassTypeArguments().get(1);
    }
}
