package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListType extends CollectionType{

    public ListType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object map(Object sourceList_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(sourceList_, mapFunction, owner);
        List sourceList = (List)sourceList_;
        List targetList = new ArrayList(sourceList.size());

        EnumerationAwareOwnerContext enumerationContext = new IndexableEnumerationOwnerContext(owner);
        for (Object sourceVal : sourceList){
            targetList.add(mapFunction.apply(sourceVal, enumerationContext));
        }
        return Collections.unmodifiableList(targetList);
    }
}
