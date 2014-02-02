package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

/**
 * Primitive or primitive box
 *
 * @author bartosz walacik
 */
public class PrimitiveType extends PrimitiveOrValueType {

    public PrimitiveType(Type baseJavaType) {
        super(baseJavaType);
    }
}
