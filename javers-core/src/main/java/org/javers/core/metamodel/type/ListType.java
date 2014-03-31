package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.IndexableOwnerContext;
import org.javers.core.metamodel.object.SimpleOwnerContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListType extends CollectionType{

    public ListType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object map(Object sourceList_, EnumerableFunction mapFunction, SimpleOwnerContext owner) {
        Validate.argumentsAreNotNull(sourceList_, mapFunction, owner);
        List sourceList = (List)sourceList_;
        List targetList = new ArrayList(sourceList.size());
        IndexableOwnerContext indexableOwnerContext = new IndexableOwnerContext(owner);

        int i = 0;
        for (Object sourceVal : sourceList){
            indexableOwnerContext.setIndex(i++);
            targetList.add(mapFunction.apply(sourceVal, indexableOwnerContext));
        }
        return targetList;
    }
}
