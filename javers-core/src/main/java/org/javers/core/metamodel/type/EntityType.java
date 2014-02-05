package org.javers.core.metamodel.type;

import org.javers.core.metamodel.property.Entity;

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
    Entity getManagedClass() {
        return (Entity)super.getManagedClass();
    }
}
