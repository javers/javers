package org.javers.core.metamodel.scanner;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bartosz.walacik
 */
class PropertyScan {
    private final List<Property> properties;
    private final List<Property> looksLikeId;

    PropertyScan(List<Property> properties) {
        Validate.argumentIsNotNull(properties);
        this.properties = properties;
        looksLikeId = new ArrayList<>();

        for (Property p : properties){
            if (p.looksLikeId()){
                looksLikeId.add(p);
            }
        }
    }

    public Property getFirst(){
        return properties.get(0);
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public List<Property> getLooksLikeId() {
        return Collections.unmodifiableList(looksLikeId);
    }

    public boolean hasId(){
        return !looksLikeId.isEmpty();
    }
}
