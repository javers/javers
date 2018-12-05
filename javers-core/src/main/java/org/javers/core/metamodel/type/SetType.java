package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Collections;

public class SetType extends CollectionType{
    public SetType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object empty() {
        return Collections.emptySet();
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        return super.mapToSet(sourceEnumerable, mapFunction, owner);
    }
}
