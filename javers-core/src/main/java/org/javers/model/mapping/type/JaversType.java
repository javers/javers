package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * Property type that can be managed by Javers, so int, String, Date, etc.
 *
 * @author bartosz walacik
 */
public abstract class JaversType {
    private Type javaType;

    protected JaversType(Type javaType) {
        this.javaType = javaType;
    }

    public Type getJavaType() {
        return javaType;
    }
}
