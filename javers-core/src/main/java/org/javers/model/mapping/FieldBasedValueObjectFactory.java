package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author pawel szymczyk
 */
public class FieldBasedValueObjectFactory extends ValueObjectFactory{

    public FieldBasedValueObjectFactory(TypeMapper typeMapper) {
        super(typeMapper, FieldBasedPropertyScanner.getInstane(typeMapper));
    }

    @Override
    public <T> ValueObject<T> create(Class<T> clazz) {
        return new ValueObject<>(clazz, propertyScanner.scan(clazz));
    }
}
