package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

public class SetType extends CollectionType{
    public SetType(Type baseJavaType) {
        super(baseJavaType);
    }
}
