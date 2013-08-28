package org.javers.model.mapping.type;

/**
 * Primitive or primitive box
 *
 * @author bartosz walacik
 */
public class PrimitiveType extends JaversType {

    public PrimitiveType(Class javaType) {
        super(javaType);
    }
}
