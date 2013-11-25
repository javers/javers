package org.javers.model.mapping;

/**
 * @author pawel szymczyk
 */
public class ValueObjectFactory extends  ManagedClassFactory<ValueObject>{

    @Override
    public <T> ValueObject<T> create(Class<T> clazz) {
        return new ValueObject<>(clazz);
    }
}
