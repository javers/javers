package org.javers.model.mapping.type;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ValueObjectType extends JaversType {
    protected ValueObjectType(Class baseJavaType) {
        super(baseJavaType);
    }
}