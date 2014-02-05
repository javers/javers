package org.javers.core.metamodel.type;

import org.javers.core.metamodel.property.ValueObject;

/**
 * @author bartosz walacik
 */
public class ValueObjectType extends ManagedType{

    public ValueObjectType(ValueObject valueObject){
        super(valueObject);
    }

    @Override
    ValueObject getManagedClass() {
        return (ValueObject)super.getManagedClass();
    }
}
