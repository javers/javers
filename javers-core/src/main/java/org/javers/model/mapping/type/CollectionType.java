package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {
    public CollectionType(Class javaType) {
        super(javaType);
    }
}
