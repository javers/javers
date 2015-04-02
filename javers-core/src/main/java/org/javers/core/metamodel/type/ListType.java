package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
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
        List sourceList = toNotNullList(sourceList_);
        List targetList = new ArrayList(sourceList.size());

        IndexableContext indexableContext = new IndexableContext();
        owner.setEnumeratorContext(indexableContext);

        for (Object sourceVal : sourceList){
            targetList.add(mapFunction.apply(sourceVal, owner));
            indexableContext.incIndex();
        }
        return Collections.unmodifiableList(targetList);
    }

    private List toNotNullList(Object sourceList) {
        if (sourceList == null) {
            return Collections.emptyList();
        }
        else{
            return (List)sourceList;
        }
    }
}
