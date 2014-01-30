package org.javers.core.metamodel.property;

import java.util.List;

/**
 * Class in client's domain model. Has list of mutable properties but no unique identifier.
 * Two valueObjects are compared property by property.
 * <p/>
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
public class ValueObject extends ManagedClass {

    public ValueObject(Class sourceClass, List<Property> properties) {
        super(sourceClass, properties);
    }
}
