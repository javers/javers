package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.ManagedClassFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.javers.common.reflection.ReflectionUtil.extractActualClassTypeArguments;
import static org.javers.common.reflection.ReflectionUtil.extractClass;

/**
 * Property type that can be managed by Javers, so int, String, Date, Reference, etc.
 * <p/>
 * Immutable
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
    protected JaversType(Type baseJavaType) {
        Validate .argumentIsNotNull(baseJavaType);

        this.baseJavaType = baseJavaType;

        this.baseJavaClass = extractClass(baseJavaType);

        this.actualClassTypeArguments = extractActualClassTypeArguments(baseJavaType);

    }

    /**
     * delegates to proper constructor {@link #JaversType(java.lang.reflect.Type)}
     */
    protected JaversType spawn(Type baseJavaType) {
            try {
                Constructor c = this.getClass().getConstructor(new Class<?>[]{Type.class});
                return (JaversType)c.newInstance(new Object[]{baseJavaType});
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException("error calling Constructor for " +
                                           this.getClass().getName()+
                                           " or no public Constructor in "+
                                           this.getClass().getName(),
                                           exception);
            }
    }

    public boolean isAssignableFrom(Class javaClass) {
        return baseJavaClass.isAssignableFrom(javaClass);
    }

    protected boolean isGenericType() {
        return (baseJavaType instanceof ParameterizedType);
    }

    /**
     * For generics, returns actual Class (type) argument
     *
     * @return Immutable List, never returns null
     */
    protected List<Class> getActualClassTypeArguments() {
        return actualClassTypeArguments;
    }

    public Type getBaseJavaType() {
        return baseJavaType;
    }

    protected Class getBaseJavaClass() {
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
        return this.getClass().getSimpleName()+ "(type:"+baseJavaType+")" ;
    }

    @Override
    public int hashCode() {
        return baseJavaType.hashCode();
    }

    @Deprecated
    boolean mayBePrototypeFor(Type javaType) {
        return !isGenericType() && isAssignableFrom(extractClass(javaType));
    }
}
