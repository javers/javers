package org.javers.common.reflection;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

import javax.persistence.Transient;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class ReflectionUtil {

    private static final Object[] EMPTY_ARRAY = new Object[]{};

    public static List<Method> findAllPersistentGetters(Class methodSource) {
        List<Method> result = new ArrayList<>();
        for(Method m : getAllMethods(methodSource)) {
             if (isPersistentGetter(m)) {
                 result.add(m);
             }
        }

        return result;
    }

    /**
     * list all class methods, including inherited and private
     */
    public static List<Method> getAllMethods(Class methodSource){
        List<Method> methods = new ArrayList<>();

        Class clazz = methodSource;
        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                methods.add(m);
            }
            clazz = clazz.getSuperclass();
        }

        return methods;
    }


    /**
     * true if method is getter and
     * <ul>
     *     <li/>is not abstract
     *     <li/>is not native
     *     <li/>doesn't have @Transient
     * </ul>
     */
    public static boolean isPersistentGetter(Method m) {
        if (!isGetter(m)){
            return false;
        }

        return (
                m.isAnnotationPresent(Transient.class) == false &&
                Modifier.isAbstract(m.getModifiers()) == false &&
                Modifier.isNative(m.getModifiers()) == false
                );
    }

    public static boolean isGetter(Method m) {
        return (m.getName().substring(0,3).equals("get")  ||
                m.getName().substring(0,2).equals("is") ) &&
                m.getParameterTypes().length == 0;
    }

    /**
     * ex: getCode() -> code,
     *     isTrue()  -> true
     */
    public static String getterToField(Method getter) {

        if (getter.getName().substring(0,3).equals("get")) {
            return getter.getName().substring(3,4).toLowerCase()+getter.getName().substring(4);
        }

        if (getter.getName().substring(0,2).equals("is")) {
            return getter.getName().substring(2,3).toLowerCase()+getter.getName().substring(3);
        }

        throw new IllegalArgumentException("Method ["+getter+"] is not getter");
    }

    public static Object invokeGetter(Method getter, Object onObject) {
        try {
            return getter.invoke(onObject, EMPTY_ARRAY);
        } catch (Exception e) {
            throw new RuntimeException("error calling getter '"+getter+"'",e);
        }
    }

    public static Object invokeGetterEvenIfPrivate(Method getter, Object onObject) {
            setAccessibleIfPrivateOrProtected(getter);
            return invokeGetter(getter, onObject);
    }

    public static Object invokeFieldEvenIfPrivate(Field field, Object onObject) {
        setAccessibleIfPrivateOrProtected(field);
        return invokeField(field, onObject);
    }

    public static Object invokeField(Field field, Object onObject) {

        try {
            return field.get(onObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("error getting unwrap from field '"+ field.getName() +"'");
        }
    }

    private static boolean isPrivateOrProtected(int modifiersCode) {
        return Modifier.isPrivate(modifiersCode) ||
                Modifier.isProtected(modifiersCode);
    }

    private static <T extends AccessibleObject & Member> void setAccessibleIfPrivateOrProtected(T object) {
        if(isPrivateOrProtected(object.getModifiers()))
        {
            object.setAccessible(true);
        }
    }

    /**
     * Makes sense only for {@link ParameterizedType}
     */
    public static List<Class> extractActualClassTypeArguments(Type javaType) {
        if (!(javaType instanceof ParameterizedType)) {
            return Collections.emptyList();
        }

        ParameterizedType parameterizedType = (ParameterizedType)javaType;

        List<Class> result = new ArrayList<>();
        for (Type t : parameterizedType.getActualTypeArguments() ) {
            if (t instanceof Class) {
                result.add((Class)t);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public static Class extractClass(Type javaType) {
        if (javaType instanceof ParameterizedType &&
                ((ParameterizedType)javaType).getRawType() instanceof Class){
            return (Class)((ParameterizedType)javaType).getRawType();
        }  else if (javaType instanceof Class) {
            return (Class)javaType;
        }

        throw new JaversException(JaversExceptionCode.CLASS_EXTRACTION_ERROR, javaType);
    }
}
