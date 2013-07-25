package org.javers.model;

import java.util.List;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Entity {

    private Class<?> sourceClass;

    private List<Property> properties;

    public Class<?> getSourceClass() {
        return sourceClass;
    }

}
