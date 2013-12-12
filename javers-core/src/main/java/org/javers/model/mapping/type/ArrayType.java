package org.javers.model.mapping.type;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class ArrayType extends ContainerType {

    public ArrayType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    protected Class initElementType() {
        return getBaseJavaClass().getComponentType();
    }

    @Override
    public boolean isAssignableFrom(Class givenType) {
        return givenType.isArray();
    }
}
