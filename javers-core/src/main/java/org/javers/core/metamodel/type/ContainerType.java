package org.javers.core.metamodel.type;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

import static org.javers.common.reflection.ReflectionUtil.extractClass;

/**
 * Collection or Array
 *
 * @author bartosz walacik
 */
public abstract class ContainerType extends EnumerableType {

    ContainerType(Type baseJavaType) {
        super(baseJavaType, 1);
    }

    /**
     * never returns null
     */
    public Type getItemType(){
        return getConcreteClassTypeArguments().get(0);
    }

    /**
     * never returns null
     */
    public Class getItemClass(){
        return extractClass(getItemType());
    }

    /**
     * Returns a List with items from sourceEnumerable mapped by mapFunction. <br/>
     * Nulls are filtered
     */
    public abstract List mapToList(Object sourceContainer, Function mapFunction);
}
