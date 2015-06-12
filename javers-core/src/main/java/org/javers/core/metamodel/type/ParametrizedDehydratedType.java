package org.javers.core.metamodel.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author bartosz walacik
 */
class ParametrizedDehydratedType implements ParameterizedType{
    private final Type rawType;
    private final Type[] actualTypeArguments;

    ParametrizedDehydratedType(Type rawType, List<Type> actualDehydratedTypeArguments) {
        this.rawType = rawType;
        this.actualTypeArguments = actualDehydratedTypeArguments.toArray(new Type[]{});
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }    
}
