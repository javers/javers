package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author pawel szymczyk
 */
public class FieldBasedValueObjectFactory extends  ValueObjectFactory{


    protected FieldBasedValueObjectFactory(TypeMapper typeMapper) {
        super(typeMapper, new FieldBasedManagedClassPropertyScanner(typeMapper));
    }

    @Override
    public <T> ValueObject<T> create(Class<T> clazz) {
        return new ValueObject(clazz, scanner.scan(clazz));
    }
}
