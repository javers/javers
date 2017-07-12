package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListType extends CollectionType{

    public ListType(Type baseJavaType) {
        super(baseJavaType);
    }

    /**
     * @return immutable List
     */
    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(mapFunction, owner);
        List sourceList = Lists.wrapNull(sourceEnumerable);
        List targetList = new ArrayList(sourceList.size());

        EnumerationAwareOwnerContext enumerationContext = new IndexableEnumerationOwnerContext(owner);
        for (Object sourceVal : sourceList){
            targetList.add(mapFunction.apply(sourceVal, enumerationContext));
        }
        return Collections.unmodifiableList(targetList);
    }

    /**
     * Nulls are filtered
     */
    @Override
    public Object map(Object sourceEnumerable, Function mapFunction) {
        List sourceCol = Lists.wrapNull(sourceEnumerable);
        return sourceCol.stream().map(mapFunction).filter(it -> it != null).collect(Collectors.toList());
    }
}
