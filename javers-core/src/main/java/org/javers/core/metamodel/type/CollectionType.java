package org.javers.core.metamodel.type;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author bartosz walacik
 */
public abstract class CollectionType extends ContainerType {

    public CollectionType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public boolean isFullyParametrized() {
        return getActualTypeArguments().size() == 1;
    }

    @Override
    public boolean isEmpty(Object collection) {
        return collection == null || ((Collection)collection).isEmpty();
    }
}
