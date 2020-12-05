package org.javers.core.metamodel.type;

import org.javers.common.collections.Lists;
import org.javers.core.metamodel.object.GlobalId;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * Type for JSON representation. Generic version of {@link ClassType#getRawDehydratedType()}
 *
 * @author bartosz.walacik
 */
class DehydratedTypeFactory {
    private static Class GLOBAL_ID_ARRAY_TYPE = new GlobalId[]{}.getClass();

    private TypeMapper mapper;

    DehydratedTypeFactory(TypeMapper mapper) {
        this.mapper = mapper;
    }

    //recursive
    public Type build(Type givenType) {
        if (givenType instanceof TypeVariable) {
            return Object.class;
        }
        final ClassType javersType = mapper.getJaversClassType(givenType);

        //for Generics, we have list of type arguments to dehydrate
        if (javersType.isGenericType()) {
            List<Type> actualDehydratedTypeArguments = extractAndDehydrateTypeArguments(javersType);
            return new ParametrizedDehydratedType(javersType.getBaseJavaClass(), actualDehydratedTypeArguments);
        }

        if (javersType instanceof ArrayType){
            Type dehydratedItemType = build( javersType.getConcreteClassTypeArguments().get(0) );
            if (dehydratedItemType == GlobalId.class){
                return GLOBAL_ID_ARRAY_TYPE;
            }
            return givenType;
        }

        return javersType.getRawDehydratedType();
    }

    private List<Type> extractAndDehydrateTypeArguments(JaversType genericType){
        return Lists.transform(genericType.getConcreteClassTypeArguments(), typeArgument -> build(typeArgument));
    }
}
