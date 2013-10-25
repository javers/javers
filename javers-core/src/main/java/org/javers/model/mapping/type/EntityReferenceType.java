package org.javers.model.mapping.type;

/**
 * Reference to {@link org.javers.model.mapping.Entity}
 *
 * @author bartosz walacik
 */
public class EntityReferenceType extends ReferenceType {
    public EntityReferenceType(Class entityClass){
        super(entityClass);
    }

    @Override
    public boolean isEntityReferenceType() {
        return true;
    }
}
