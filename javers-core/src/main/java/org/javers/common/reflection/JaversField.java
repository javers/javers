package org.javers.common.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

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

    public Object invokeFieldEvenIfPrivate(Object onObject) {
        setAccessibleIfNecessary();

        try {
            return getRawMember().get(onObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("error getting value from field {"+ this +"}",e);
        }
    }

    @Override
    public String toString() {
        return "Field " + getGenericType()+" " + name() +"; //declaring class:" +getDeclaringClass().getName();
    }
}
