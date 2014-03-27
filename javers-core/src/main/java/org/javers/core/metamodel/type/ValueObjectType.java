package org.javers.core.metamodel.type;

import org.javers.core.metamodel.property.ManagedClassFactory;
import org.javers.core.metamodel.property.ValueObject;

/**
 * @author bartosz walacik
 */
public class ValueObjectType extends ManagedType{

    public ValueObjectType(ValueObject valueObject){
        super(valueObject);
    }

    @Override
    public ValueObject getManagedClass() {
        return (ValueObject)super.getManagedClass();
    }

    @Override
    ManagedType spawn(Class javaType, ManagedClassFactory managedClassFactory) {
        return new ValueObjectType(managedClassFactory.createValueObject(javaType));
    }
}
