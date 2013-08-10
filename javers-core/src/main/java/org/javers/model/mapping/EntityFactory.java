package org.javers.model.mapping;

/**
 * @author bartosz walacik
 */
public interface EntityFactory {
    <S> Entity<S> create(Class<S> beanClass);
}
