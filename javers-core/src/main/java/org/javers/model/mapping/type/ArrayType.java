package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class ArrayType extends ContainerType {
    private transient Class elementType;

    public ArrayType(Type baseJavaType) {
        super(baseJavaType);
        elementType = getBaseJavaClass().getComponentType();

    }

    @Override
    public boolean isAssignableFrom(Class givenType) {
        return givenType.isArray();
    }

    @Override
    public Class getElementType() {
        return elementType;
    }
}
