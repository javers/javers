package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListType extends CollectionType{

    public ListType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object map(Object sourceList_, EnumerableFunction mapFunction) {
        Validate.argumentsAreNotNull(sourceList_, mapFunction);
        List sourceList = (List)sourceList_;
        List targetList = new ArrayList(sourceList.size());

        int i = 0;
        for (Object sourceVal : sourceList){
            targetList.add(mapFunction.apply(sourceVal,""+i++));
        }
        return targetList;
    }
}
