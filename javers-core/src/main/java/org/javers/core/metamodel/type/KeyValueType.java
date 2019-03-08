package org.javers.core.metamodel.type;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

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

    /**
     * null keys are filtered
     */
    public abstract List mapToList(Object kv, Function mapFunction);
}
