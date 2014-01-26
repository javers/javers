package org.javers.core.metamodel.property;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.model.mapping.type.JaversType;

import javax.persistence.Id;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Immutable
 *
 * @author bartosz walacik
 */
public class BeanProperty implements Property {

    private transient final Method getter;

    private final String name;

    private transient final JaversType javersType;

    protected BeanProperty(Method getter, JaversType javersType) {

        argumentIsNotNull(getter, "getter should not be null!");
        argumentIsNotNull(javersType, "javersType should not be null!");

        this.getter = getter;
        this.name = ReflectionUtil.getterToField(getter);
        this.javersType = javersType;
    }

    @Override
    public Type getGenericType() {
        return getter.getGenericReturnType();
    }

    @Override
    public Class<?> getType() {
        return getter.getReturnType();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object get(Object target) {
        return ReflectionUtil.invokeGetterEvenIfPrivate(getter, target);
    }

    @Override
    public boolean isNull(Object target) {
        return get(target) == null;
    }

    @Override
    public boolean looksLikeId() {
        return getter.isAnnotationPresent(Id.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanProperty that = (BeanProperty) o;
        return getter.equals(that.getter) && javersType.equals(that.javersType);
    }

    @Override
    public int hashCode() {
        return 31 * getter.hashCode() + javersType.hashCode();
    }

    @Override
    public String toString() {
        return getter.getDeclaringClass().getSimpleName()+"."+getter.getName()+"()";
    }
}