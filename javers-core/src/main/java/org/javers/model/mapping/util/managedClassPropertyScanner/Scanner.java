package org.javers.model.mapping.util.managedClassPropertyScanner;

import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.TypeMapper;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public abstract class Scanner {

    protected TypeMapper typeMapper;

    protected Scanner(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public abstract <S> List<Property> scan(Class<S> entityClass);
}
