package org.javers.model.mapping;

import java.util.Collections;
import java.util.List;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Entity {

    private Class<?> sourceClass;

    private List<Property> properties;

    public Entity(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }

    public Class<?> getSourceClass() {
        return sourceClass;
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }
}