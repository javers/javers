package org.javers.model.mapping.type;

import org.javers.model.mapping.Entity;

import java.lang.reflect.Type;

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
