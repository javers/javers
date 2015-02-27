package org.javers.core.metamodel.type;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
class ParametrizedDehydratedType implements ParameterizedType{
    private final Type rawType;
    private final Type[] actualTypeArguments;

    ParametrizedDehydratedType(JaversType javersType, final TypeMapper typeMapper) {
        this.rawType = javersType.getBaseJavaClass();

        this.actualTypeArguments =
                Lists.transform(javersType.getActualTypeArguments(), new Function<Type, Type>() {
                    public Type apply(Type input) {
                        return typeMapper.getJaversType(input).getRawDehydratedType();
                    }
                }).toArray(new Type[]{});
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
