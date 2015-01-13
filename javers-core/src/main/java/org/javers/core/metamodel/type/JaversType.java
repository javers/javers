package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.javers.common.reflection.ReflectionUtil.extractActualClassTypeArguments;
import static org.javers.common.reflection.ReflectionUtil.extractClass;

/**
 * Managed property type
 * <br/><br/>
 *
 * This is convenient abstraction layer over raw and awkward
 * java.lang.reflect.Type and java.lang.Class
 *
 * @author bartosz walacik
 */
public abstract class JaversType {
    private final Type  baseJavaType;
    private final Class baseJavaClass;
    private final List<Class> actualClassTypeArguments;

    /**
     * @param baseJavaType Class or ParametrizedType
     */
    JaversType(Type baseJavaType) {
        Validate .argumentIsNotNull(baseJavaType);

        this.baseJavaType = baseJavaType;
        this.baseJavaClass = extractClass(baseJavaType);
        this.actualClassTypeArguments = extractActualClassTypeArguments(baseJavaType);
    }

    /**
     * Factory method, delegates to self constructor
     */
    JaversType spawn(Type baseJavaType) {
        try {
            Constructor c = this.getClass().getConstructor(new Class<?>[]{Type.class});
            return (JaversType)c.newInstance(new Object[]{baseJavaType});
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("error calling Constructor for " + this.getClass().getName(), exception);
        }
    }

    public boolean isGenericType() {
        return (baseJavaType instanceof ParameterizedType);
    }

    /**
     * For generics, returns actual Class (type) argument
     *
     * @return Immutable List, never returns null
     */
    public List<Class> getActualClassTypeArguments() {
        return actualClassTypeArguments;
    }

    public Type getBaseJavaType() {
        return baseJavaType;
    }

    public Class getBaseJavaClass() {
        return baseJavaClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JaversType that = (JaversType) o;
        return baseJavaType.equals(that.baseJavaType);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+ "(type:"+baseJavaType+")" ;
    }

    @Override
    public int hashCode() {
        return baseJavaType.hashCode();
    }
}
