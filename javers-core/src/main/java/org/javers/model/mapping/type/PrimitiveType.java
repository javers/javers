package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * Primitive or primitive box
 *
 * @author bartosz walacik
 */
public class PrimitiveType extends JaversType {
    public PrimitiveType(Type javaType) {
        super(javaType);
    }
}
