package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class PrimitiveOrValueType extends JaversType{
    public PrimitiveOrValueType(Type baseJavaType) {
        super(baseJavaType);
    }
}
