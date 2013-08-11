package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public abstract class ContainerType extends JaversType {

    protected ContainerType(Class baseJavaType) {
        super(baseJavaType);
    }
}
