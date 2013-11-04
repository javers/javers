package org.javers.model.mapping.type;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ValueObjectType extends ReferenceType {
    protected ValueObjectType(Class baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public boolean isValueObject() {
        return true;
    }
}