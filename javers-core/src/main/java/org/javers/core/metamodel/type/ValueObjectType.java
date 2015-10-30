package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;

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

    ValueObjectType(ManagedClass valueObject){
        super(valueObject);
    }

    ValueObjectType(ManagedClass valueObject, Optional<String> typeName) {
        super(valueObject, typeName);
    }

    @Override
    ValueObjectType spawn(ManagedClass managedClass) {
        return new ValueObjectType(managedClass);
    }
}
