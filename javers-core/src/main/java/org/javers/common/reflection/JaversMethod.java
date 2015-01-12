package org.javers.common.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class JaversMethod extends JaversMember<Method> {
    private static final Object[] EMPTY_ARRAY = new Object[]{};

    public JaversMethod(Method rawMethod, Type resolvedReturnType) {
        super(rawMethod, resolvedReturnType);
    }

    @Override
    protected Type getRawGenericType() {
        return getRawMember().getGenericReturnType();
    }

    @Override
    public Class<?> getType() {
        return getRawMember().getReturnType();
    }

    public Object invokeGetterEvenIfPrivate(Object onObject) {
        setAccessibleIfNecessary();

        try {
            return getRawMember().invoke(onObject, EMPTY_ARRAY);
        } catch (Exception e) {
            throw new RuntimeException("error calling getter {"+ this +"}",e);
        }
    }

    @Override
    public String toString() {
        return "Method " + getGenericType()+" " + name() +"(); //declaring class: " +getDeclaringClass().getName();
    }
}
