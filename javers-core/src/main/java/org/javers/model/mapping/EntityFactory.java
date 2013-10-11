package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author bartosz walacik
 */
public abstract class EntityFactory extends ManagedClassFactory<Entity>{

    protected EntityFactory(TypeMapper typeMapper, PropertyScanner propertyScanner) {
        super(typeMapper, propertyScanner);
    }

    public <S> Entity<S> createEntity(Class<S> entityClass){
        return create(entityClass);
    }
}
