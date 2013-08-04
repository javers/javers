package org.javers.model.mapping;

/**
 * @author bartosz walacik
 */
public class EntityFactory {
    protected <S> Entity<S> createFromBean(Class<S> beanClass) {
        Entity entity = new Entity(beanClass);

        return entity;
    }
}
