package org.javers.model.mapping;

import org.javers.model.mapping.Property;
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

    public abstract <S> List<Property> scan(Class<S> entityClass);
}
