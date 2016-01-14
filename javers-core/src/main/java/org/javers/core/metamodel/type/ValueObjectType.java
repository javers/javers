package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.property.Property;

import java.util.List;

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

    public ValueObjectType(Class baseJavaClass, List<Property> allProperties){
        this(new ManagedClass(baseJavaClass, allProperties));
    }


    ValueObjectType(ManagedClass valueObject, Optional<String> typeName) {
        super(valueObject, typeName);
    }

    @Override
    ValueObjectType spawn(ManagedClass managedClass, Optional<String> typeName) {
        return new ValueObjectType(managedClass, typeName);
    }
}
