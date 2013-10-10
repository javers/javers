package org.javers.common.scanner;

import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.TypeMapper;

import java.util.List;

/**
 * Managed class property scanner
 *
 * @author pawel szymczyk
 */
public abstract class Scanner {

    protected TypeMapper typeMapper;

    protected Scanner(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public abstract <S> List<Property> scan(Class<S> entityClass);
}
