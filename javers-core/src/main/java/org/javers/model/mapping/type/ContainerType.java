package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class ContainerType extends JaversType{
    public ContainerType(Type javaType) {
        super(javaType);
    }
}
