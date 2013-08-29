package org.javers.model.mapping.type;

/**
 * Reference to {@link org.javers.model.mapping.Entity}
 *
 * @author bartosz walacik
 */
public class ReferenceType extends JaversType {;
    public ReferenceType(Class entityClass){
        super(entityClass);
    }

}
