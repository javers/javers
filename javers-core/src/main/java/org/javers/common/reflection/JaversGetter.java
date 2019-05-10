package org.javers.common.reflection;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.javers.common.string.ToStringBuilder.typeName;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author bartosz walacik
 */
public class JaversGetter extends JaversMember<Method> {
    private static final Logger logger = getLogger(JaversGetter.class);

    private static final Object[] EMPTY_ARRAY = new Object[]{};

    private final Optional<Method> setterMethod;

    protected JaversGetter(Method getterMethod, Type resolvedReturnType) {
        super(getterMethod, resolvedReturnType);
        setterMethod = findSetterForGetter(getterMethod);
    }

    protected JaversGetter(Method getterMethod, Type resolvedReturnType, boolean looksLikeId) {
        super(getterMethod, resolvedReturnType, looksLikeId);
        setterMethod = findSetterForGetter(getterMethod);
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
    public Object getEvenIfPrivate(Object onObject) {
        try {
            return getRawMember().invoke(onObject, EMPTY_ARRAY);
        } catch (IllegalArgumentException ie) {
            return getOnMissingProperty(onObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new JaversException(JaversExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public void setEvenIfPrivate(Object onObject, Object value) {
        setterMethod.orElseThrow(() -> new JaversException(JaversExceptionCode.SETTER_NOT_FOUND,
                getRawMember().getName(), getRawMember().getDeclaringClass().getName()));

        try {
            setterMethod.get().invoke(onObject, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new JaversException(JaversExceptionCode.SETTER_INVOCATION_ERROR,
                    setterMethod.get().getName(), onObject.getClass().getName(), e);
        }
    }

    @Override
    public String propertyName() {
        return getterNameToFieldName(name());
    }

    private Optional<Method> findSetterForGetter(Method getter) {
        Class<?> clazz = getter.getDeclaringClass();
        String setterName = setterNameForGetterName(getter.getName());
        try {
            Method setter = clazz.getDeclaredMethod(setterName, getter.getReturnType());
            setAccessibleIfNecessary(setter);
            return Optional.of(setter);
        } catch (NoSuchMethodException e) {
            logger.debug("setter for getter '" + clazz.getName() + "." + getter.getName() + " not found");
            return Optional.empty();
        }
    }

    private String getterNameToFieldName(String getterName) {
        String withoutPrefix = getterNameWithoutPrefix(getterName);
        return withoutPrefix.substring(0, 1).toLowerCase() + withoutPrefix.substring(1);
    }

    private static String setterNameForGetterName(String getterName) {
        return "set" + getterNameWithoutPrefix(getterName);
    }

    private static String getterNameWithoutPrefix(String getterName) {
        if (getterName.substring(0, 3).equals("get")) {
            return getterName.substring(3);
        }

        if (getterName.substring(0, 2).equals("is")) {
            return getterName.substring(2);
        }

        throw new IllegalArgumentException("Name {"+getterName+"} is not a getter name");
    }

    @Override
    public String memberType() {
        return "Getter";
    }
}
