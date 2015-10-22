package org.javers.core.metamodel.type;

import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author bartosz walacik
 */
@Deprecated
class ValueObject extends ManagedClass {

    ValueObject(Class clientsClass, List<Property> properties) {
        super(clientsClass, properties);
    }
}
