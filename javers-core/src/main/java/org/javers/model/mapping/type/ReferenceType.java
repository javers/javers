package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * Reference to {@link org.javers.model.mapping.Entity}
 *
 * @author bartosz walacik
 */
public class ReferenceType extends JaversType {

    @Override
    public boolean isMappingForJavaType(Class givenType) {
        throw new RuntimeException("not implemented") ;
    }

    public ReferenceType(Class javaType) {
        super(javaType);
    }
}
