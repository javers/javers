package org.javers.model.mapping;

import org.javers.core.metamodel.property.Property;

import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

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
