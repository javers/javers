package org.javers.core.metamodel.property;

import org.javers.common.reflection.ReflectionUtil;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Immutable
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class FieldProperty implements Property {

    private transient final Field field;

    public FieldProperty(Field field) {

        argumentIsNotNull(field, "field should not be null!");

        this.field = field;
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public boolean looksLikeId() {
        return field.isAnnotationPresent(Id.class);
    }

    @Override
    public Object get(Object target) {
        return ReflectionUtil.invokeFieldEvenIfPrivate(field, target);
    }

    @Override
    public boolean isNull(Object target) {
        return get(target) == null;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldProperty that = (FieldProperty) o;
        return field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    public String toString() {
        return field.getDeclaringClass().getSimpleName()+"."+field.getName();
    }
}
