package org.javers.model.mapping.type;

/**
 * Property type that can be managed by Javers, so int, String, Date, etc.
 *
 * Immutable
 *
 * @author bartosz walacik
 */
public abstract class JaversType {
    protected final Class baseJavaType;

    protected JaversType(Class baseJavaType) {
        this.baseJavaType = baseJavaType;
    }

    public boolean isMappingForJavaType(Class givenType) {
        return baseJavaType.isAssignableFrom(givenType);
    }

    public Class getBaseJavaType() {
        return baseJavaType;
    }
}
