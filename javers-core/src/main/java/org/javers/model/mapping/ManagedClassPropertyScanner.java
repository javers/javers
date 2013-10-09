package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public abstract class ManagedClassPropertyScanner {

    public abstract <S> List<Property> scan(Class<S> entityClass);
}
