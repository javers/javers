package org.javers.core.metamodel.property;

import org.javers.common.reflection.JaversMethod;
import org.javers.common.reflection.ReflectionUtil;

import java.lang.reflect.Type;

import static org.javers.common.reflection.ReflectionUtil.ID_ANN;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Immutable
 *
 * @author bartosz walacik
 */
public class BeanProperty implements Property {

    private transient final JaversMethod getter;
    private final String name;

    BeanProperty(JaversMethod getter) {
        argumentIsNotNull(getter, "getter should not be null!");

        this.getter = getter;
        this.name = ReflectionUtil.getterToField(getter.name());
    }

    @Override
    public Type getGenericType() {
        return getter.getGenericType();
    }

    @Override
    public Class<?> getType() {
        return getter.getType();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object get(Object target) {
        return getter.invokeGetterEvenIfPrivate(target);
    }

    @Override
    public boolean isNull(Object target) {
        return get(target) == null;
    }

    @Override
    public boolean looksLikeId() {
        return getter.isAnnotationPresent(ID_ANN);
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
        return getter.toString();
    }
}