package org.javers.core.metamodel.type;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Type for JSON representation. Generic version of {@link JaversType#getRawDehydratedType()}
 *
 * @author bartosz.walacik
 */
class DehydratedTypeBuilder {

    private TypeMapper mapper;

    DehydratedTypeBuilder(TypeMapper mapper) {
        this.mapper = mapper;
    }

    //recursive
    public Type build(Type forType){
        final JaversType javersType = mapper.getJaversType(forType);

        if (!javersType.isGenericType()){
            return javersType.getRawDehydratedType();
        }

        Type rawType = javersType.getBaseJavaClass();
        List<Type> actualDehydratedTypeArguments = extractAndDehydrateTypeArguments(javersType);

        return new ParametrizedDehydratedType(rawType, actualDehydratedTypeArguments);
    }

    private List<Type> extractAndDehydrateTypeArguments(JaversType genericType){
        return Lists.transform(genericType.getActualTypeArguments(), new Function<Type, Type>() {
                public Type apply(Type typeArgument) {
                    return build(typeArgument);
                }
        });
    }
}
