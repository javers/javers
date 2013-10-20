package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author pawel szymczyk
 */
public class ValueObjectFactory extends  ManagedClassFactory<ValueObject>{

    protected TypeMapper typeMapper;

    protected ValueObjectFactory(TypeMapper typeMapper, PropertyScanner propertyScanner) {
        super(typeMapper, propertyScanner);
    }

    @Override
    public <T> ValueObject<T> create(Class<T> clazz) {
        return new ValueObject<>(clazz, propertyScanner.scan(clazz));
    }
}
