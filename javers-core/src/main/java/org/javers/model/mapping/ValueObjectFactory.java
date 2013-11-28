package org.javers.model.mapping;

/**
 * @author pawel szymczyk
 */
public class ValueObjectFactory {
    public <T> ValueObject<T> create(Class<T> clazz) {
        return new ValueObject<>(clazz);
    }
}
