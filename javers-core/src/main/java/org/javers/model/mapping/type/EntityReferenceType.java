package org.javers.model.mapping.type;

/**
 * Reference to {@link org.javers.model.mapping.Entity}
 *
 * @author bartosz walacik
 */
public class EntityReferenceType extends JaversType {;
    public EntityReferenceType(Class entityClass){
        super(entityClass);
    }

}
