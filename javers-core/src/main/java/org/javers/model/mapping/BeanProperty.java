package org.javers.model.mapping;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.model.mapping.type.JaversType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.Id;
import java.lang.reflect.Method;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Immutable
 *
 * @author bartosz walacik
 */
public class BeanProperty implements Property {
    private final Method getter;
    private final String name;
    private final JaversType javersType;

    protected BeanProperty(Method getter, JaversType javersType) {

        argumentIsNotNull(getter, "getter should not be null!");
        argumentIsNotNull(javersType, "javersType should not be null!");

        this.getter = getter;
        this.name = ReflectionUtil.getterToField(getter);
        this.javersType = javersType;
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
    public JaversType getType() {
        return javersType;
    }

    @Override
    public void setValue(Object value) {
        throw new NotImplementedException();
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
}