package org.javers.model.mapping;

/**
 * @author pawel szymczyk
 */
public class ValueObjectFactory extends  ManagedClassFactory<ValueObject>{

    public ValueObjectFactory(PropertyScanner propertyScanner) {
        super(propertyScanner);
    }

    @Override
    public <T> ValueObject<T> create(Class<T> clazz) {
        return new ValueObject<>(clazz, propertyScanner.scan(clazz));
    }
}
