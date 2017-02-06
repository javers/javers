package org.javers.core.metamodel.scanner;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.Collections;
import java.util.List;

/**
 * @author bartosz.walacik
 */
class PropertyScan {
    private final List<Property> properties;
    private final boolean hasId;

    PropertyScan(List<Property> properties) {
        Validate.argumentIsNotNull(properties);
        this.properties = properties;

        hasId = properties.stream().anyMatch(p -> p.looksLikeId());
    }

    public Property getFirst(){
        return properties.get(0);
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public boolean hasId(){
        return hasId;
    }
}
