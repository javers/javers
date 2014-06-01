package org.javers.core.metamodel.property;

import org.javers.common.reflection.ReflectionUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.javers.common.reflection.ReflectionUtil.ID_ANN;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Immutable
 *
 * @author bartosz walacik
 */
public class BeanProperty implements Property {

    private transient final Method getter;
    private final String name;

    BeanProperty(Method getter) {
        argumentIsNotNull(getter, "getter should not be null!");

        this.getter = getter;
        this.name = ReflectionUtil.getterToField(getter);
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
        return ReflectionUtil.isAnnotationPresent(getter, ID_ANN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanProperty that = (BeanProperty) o;
        return getter.equals(that.getter);
    }

    @Override
    public int hashCode() {
        return getter.hashCode();
    }

    @Override
    public String toString() {
        return getter.getGenericReturnType()+" "+ getter.getDeclaringClass().getSimpleName()+"."+getter.getName()+"()";
    }
}