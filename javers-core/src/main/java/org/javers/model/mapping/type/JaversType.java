package org.javers.model.mapping.type;

import org.javers.core.diff.appenders.PropertyChangeAppender;

/**
 * Property type that can be managed by Javers, so int, String, Date, etc.
 * <p/>
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JaversType that = (JaversType) o;
        return baseJavaType.equals(that.baseJavaType);
    }

    @Override
    public int hashCode() {
        return baseJavaType.hashCode();
    }
}
