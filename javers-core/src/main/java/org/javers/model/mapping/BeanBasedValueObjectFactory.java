package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author pawel szymczyk
 */
public class BeanBasedValueObjectFactory extends ValueObjectFactory{

    public BeanBasedValueObjectFactory(TypeMapper typeMapper) {
        super(typeMapper, BeanBasedPropertyScanner.getInstane(typeMapper));
    }

    @Override
    public <T> ValueObject<T> create(Class<T> clazz) {
        return new ValueObject<>(clazz, propertyScanner.scan(clazz));
    }
}
