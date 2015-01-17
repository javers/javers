package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class CustomType extends JaversType {
    public CustomType(Type baseJavaType) {
        super(baseJavaType);
    }
}