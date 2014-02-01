package org.javers.core.metamodel.type;

/**
 * Reference to {@link org.javers.core.metamodel.property.Entity}
 *
 * @author bartosz walacik
 */
public class EntityReferenceType extends JaversType {

    public EntityReferenceType(Class entityClass){
        super(entityClass);
    }
}
