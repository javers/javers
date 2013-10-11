package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author pawel szymczyk
 */
public abstract class ValueObjectFactory extends  ManagedClassFactory<ValueObject>{

    protected TypeMapper typeMapper;

    protected ValueObjectFactory(TypeMapper typeMapper, PropertyScanner propertyScanner) {
        super(typeMapper, propertyScanner);
    }

    public abstract <T> ValueObject<T> create(Class<T> clazz);
}
