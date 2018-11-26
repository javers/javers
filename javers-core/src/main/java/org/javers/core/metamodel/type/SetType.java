package org.javers.core.metamodel.type;

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
}
