package org.javers.core.metamodel.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import static org.javers.common.reflection.ReflectionUtil.extractClass;

/**
 * Collection or Array
 *
 * @author bartosz walacik
 */
public abstract class ContainerType extends EnumerableType {

    ContainerType(Type baseJavaType, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, 1, typeMapperLazy);
    }

    public Type getItemJavaType(){
        return getConcreteClassTypeArguments().get(0);
    }

    public JaversType getItemJaversType() {
        return getTypeMapperLazy().getJaversType(getItemJavaType());
    }

    /**
     * never returns null
     */
    public Class getItemClass(){
        return extractClass(getItemJavaType());
    }
}
