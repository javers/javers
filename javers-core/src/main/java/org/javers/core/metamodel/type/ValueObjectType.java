package org.javers.core.metamodel.type;

import org.javers.core.metamodel.clazz.ManagedClassFactory;
import org.javers.core.metamodel.clazz.ValueObject;

/**
 * ValueObject class in client's domain model.
 * <br/><br/>
 *
 * Has list of mutable properties but no unique identifier.
 * <br/><br/>
 *
 * Two ValueObjects are compared property by property.
 * <br/><br/>
 *
 * Example:
 * <pre>
 *     class Address {
 *         private String city;
 *         private String street;
 *         private String zip;
 *         ...
 *     }
 * </pre>
 *
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
