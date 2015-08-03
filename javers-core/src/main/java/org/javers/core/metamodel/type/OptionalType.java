package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;

/**
 * @author bartosz.walacik
 */
public class OptionalType extends CollectionType {

    public OptionalType(Type baseJavaType) {
        super(baseJavaType);
    }

    public OptionalType() {
        super(java.util.Optional.class);
    }

    @Override
    public boolean isFullyParametrized() {
        return getActualTypeArguments().size() == 1;
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        return new RuntimeException("not implemented");
    }
}
