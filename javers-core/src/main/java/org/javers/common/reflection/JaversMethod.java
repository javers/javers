package org.javers.common.reflection;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.javers.common.string.ToStringBuilder.typeName;

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
    public Class<?> getRawType() {
        return getRawMember().getReturnType();
    }

    @Override
    public Object invokeEvenIfPrivate(Object onObject) {
        setAccessibleIfNecessary();

        try {
            return getRawMember().invoke(onObject, EMPTY_ARRAY);
        } catch (IllegalArgumentException ie){
            throw new JaversException(JaversExceptionCode.MISSING_PROPERTY, this, ie.getClass().getName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new JaversException(JaversExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public String propertyName() {
        return getterToField(name());
    }

    @Override
    public String toString() {
        return "Method " + typeName(getGenericResolvedType())+" " + name() +"; //declared in: " +getDeclaringClass().getSimpleName();
    }

    /**
     * ex: getCode() -> code,
     *     isTrue()  -> true
     */
    private String getterToField(String getterName) {

        if (getterName.substring(0, 3).equals("get")) {
            return getterName.substring(3, 4).toLowerCase()+getterName.substring(4);
        }

        if (getterName.substring(0, 2).equals("is")) {
            return getterName.substring(2, 3).toLowerCase()+getterName.substring(3);
        }

        throw new IllegalArgumentException("Name {"+getterName+"} is not a getter name");
    }
}
