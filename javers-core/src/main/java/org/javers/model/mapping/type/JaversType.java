package org.javers.model.mapping.type;

import org.javers.common.validation.Validate;
import org.javers.core.diff.appenders.PropertyChangeAppender;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Property type that can be managed by Javers, so int, String, Date, etc.
 * <p/>
 * Immutable
 *
 * @author bartosz walacik
 */
public abstract class JaversType {
    protected final Type  baseJavaType;
    protected final Class baseJavaClass;

    /**
     * @param baseJavaType Class or ParametrizedType
     */
    protected JaversType(Type baseJavaType) {
        Validate .argumentIsNotNull(baseJavaType);

        this.baseJavaType = baseJavaType;

        if (baseJavaType instanceof ParameterizedType) {
            this.baseJavaClass = (Class)((ParameterizedType) baseJavaType).getRawType();
        } else if (baseJavaType instanceof Class) {
            this.baseJavaClass = (Class)baseJavaType;
        }else {
            throw new IllegalArgumentException("don't know kow to handle type: "+baseJavaType+". Expected Class|ParametrizedType");
        }
    }

    public boolean isGeneric() {
        return (baseJavaType instanceof  ParameterizedType);
    }

    @Deprecated
    public boolean isMappingForJavaClass(Class givenJavaClass) {
        return baseJavaClass.isAssignableFrom(givenJavaClass);
    }

    public Type getBaseJavaType() {
        return baseJavaType;
    }

    public Class getBaseJavaClass() {
        return baseJavaClass;
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
    public String toString() {
        return this.getClass().getSimpleName()+ "(type:"+baseJavaType+", class:"+baseJavaClass.getName()+")" ;
    }

    @Override
    public int hashCode() {
        return baseJavaType.hashCode();
    }
}
