package org.javers.core.metamodel.clazz;

import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ValueObject extends ManagedClass {

    ValueObject(Class clientsClass, List<Property> properties) {
        super(clientsClass, properties);
    }
}
