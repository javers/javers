package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class ImmutableValueType extends JaversType {
    protected ImmutableValueType(Type baseJavaType) {
        super(baseJavaType);
    }
}
