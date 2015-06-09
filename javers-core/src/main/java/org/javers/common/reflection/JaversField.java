package org.javers.common.reflection;

import org.javers.common.exception.JaversGetterException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.javers.common.string.ToStringBuilder.typeName;

/**
 * @author bartosz walacik
 */
public class JaversField extends JaversMember<Field> {

    public JaversField(Field rawField, Type resolvedReturnType) {
        super(rawField, resolvedReturnType);
    }

    @Override
    protected Type getRawGenericType() {
        return getRawMember().getGenericType();
    }

    @Override
    public Class<?> getType() {
        return getRawMember().getType();
    }

    @Override
    public Object invokeEvenIfPrivate(Object onObject) {
        setAccessibleIfNecessary();

        try {
            return getRawMember().get(onObject);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new JaversGetterException("error getting value from the field {"+ this +"}, " + e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Field " + typeName(getGenericType())+" " + name() +"; //declared in: " +getDeclaringClass().getSimpleName();
    }
}
