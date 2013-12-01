package org.javers.model.mapping.type;

import org.javers.core.diff.appenders.PropertyChangeAppender;

/**
 * Reference to {@link org.javers.model.mapping.Entity}
 *
 * @author bartosz walacik
 */
public class EntityReferenceType extends JaversType {

    public EntityReferenceType(Class entityClass){
        super(entityClass);
    }
}
