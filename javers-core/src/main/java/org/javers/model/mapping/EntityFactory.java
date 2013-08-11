package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author bartosz walacik
 */
public abstract class EntityFactory {
    protected TypeMapper typeMapper;

    protected EntityFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public abstract <S> Entity<S> create(Class<S> beanClass);
}
