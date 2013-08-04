package org.javers.model.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Entity<S> {

    private Class<S> sourceClass;

    private List<Property> properties = new ArrayList<>();

    public Entity(Class<S> sourceClass) {
        this.sourceClass = sourceClass;
    }

    protected Class<S> getSourceClass() {
        return sourceClass;
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public Property getProperty(String withName) {
        Property found = null;
        for (Property property : properties) {
            if (property.getName().equals(withName)) {
                found = property;
            }
        }
        return found;
    }
}