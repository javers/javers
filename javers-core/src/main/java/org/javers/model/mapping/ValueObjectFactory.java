package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

import java.util.ArrayList;

/**
 * @author pawel szymczyk
 */
public abstract class ValueObjectFactory extends  ManagedClassFactory<ValueObject>{

    protected TypeMapper typeMapper;

    protected ValueObjectFactory(TypeMapper typeMapper, ManagedClassPropertyScanner scanner) {
        super(typeMapper, scanner);
    }

    public abstract <T> ValueObject<T> create(Class<T> clazz);
}
