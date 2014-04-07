package org.javers.core.metamodel.type;

import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClassFactory;

import java.lang.reflect.Type;

/**
 * Reference to {@link Entity}
 *
 * @author bartosz walacik
 */
public class EntityType extends ManagedType {

    public EntityType(Entity entity){
        super(entity);
    }

    @Override
    public Entity getManagedClass() {
        return (Entity)super.getManagedClass();
    }

    @Override
    ManagedType spawn(Class javaType, ManagedClassFactory managedClassFactory) {
        return new EntityType(managedClassFactory.createEntity(javaType));
    }
}
