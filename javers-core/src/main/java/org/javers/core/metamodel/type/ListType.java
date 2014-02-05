package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

public class ListType extends CollectionType{

    public ListType(Type baseJavaType) {
        super(baseJavaType);
    }
}
