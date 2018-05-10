package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

public class ListAsSetType extends CollectionType {

    public ListAsSetType(Type baseJavaType) {
        super(baseJavaType);
    }
}
