package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * Reference to {@link org.javers.model.mapping.Entity}
 *
 * @author bartosz walacik
 */
public class ReferenceType extends JaversType {

    public ReferenceType(Type javaType) {
        super(javaType);
    }
}
