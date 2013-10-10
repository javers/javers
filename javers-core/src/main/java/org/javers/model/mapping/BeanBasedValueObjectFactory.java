package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.javers.common.scanner.BeanBasedScanner;

/**
 * @author pawel szymczyk
 */
public class BeanBasedValueObjectFactory extends ValueObjectFactory{

    public BeanBasedValueObjectFactory(TypeMapper typeMapper) {
        super(typeMapper, BeanBasedScanner.getInstane(typeMapper));
    }

    @Override
    public <T> ValueObject<T> create(Class<T> clazz) {
        return new ValueObject<>(clazz, scanner.scan(clazz));
    }
}
