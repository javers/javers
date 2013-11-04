package org.javers.model.mapping.type;

import java.lang.reflect.Array;

/**
 * @author bartosz walacik
 */
public class ArrayType extends ContainerType {
    public ArrayType() {
        super(Array.class);
    }

    @Override
    public boolean isMappingForJavaType(Class givenType) {
        return givenType.isArray();
    }
}
