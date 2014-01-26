package org.javers.core.metamodel.property;

import org.javers.model.mapping.type.TypeMapper;

import java.util.List;

/**
 * Managed class property propertyScanner
 *
 * @author pawel szymczyk
 */
public abstract class PropertyScanner {

    protected TypeMapper typeMapper;

    protected PropertyScanner(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public abstract List<Property> scan(Class<?> managedClass);
}
