package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static org.javers.common.validation.Validate.argumentIsNotNull;

public class ListAsSetType extends CollectionType {

    public ListAsSetType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public boolean isInstance(Object cdo) {
        return cdo instanceof Set || cdo instanceof List;
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        return super.mapToSet(sourceEnumerable, mapFunction, owner);
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return List.class;
    }
}
