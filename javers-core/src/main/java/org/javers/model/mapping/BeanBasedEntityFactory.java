package org.javers.model.mapping;

/**
 * @author bartosz walacik
 */
public class BeanBasedEntityFactory implements EntityFactory {

    public <S> Entity<S> create(Class<S> beanClass) {
        Entity entity = new Entity(beanClass);
        return entity;
    }
}
