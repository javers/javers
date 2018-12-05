package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;

public class ListAsSetType extends CollectionType {

    public ListAsSetType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        return super.mapToSet(sourceEnumerable, mapFunction, owner);
    }
}
