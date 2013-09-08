package org.javers.common.reflection;

import javax.persistence.Transient;
import java.lang.reflect.*;
import java.util.ArrayList;
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
            throw new RuntimeException("error getting value from field '"+ field.getName() +"'");
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
     * Returns a class that represents the declared type for the field represented by this object.
     * For example if the field is java.util.List<org.package.model.DummyObject> the result will be DummyObject.
     */
    public static Class getGenericTypeClass(Type type) {
        if(type instanceof ParameterizedType) {
            return getClassFromParametrizedTypeArgument((ParameterizedType) type);
        }
        throw new IllegalArgumentException("Error can not get any additional data from this type " + type.getClass()
                +".\nArgument type (java.lang.reflect.Type) should be obtain by invoke java.lang.reflect.Field.getGenericType() or java.lang.reflect.Method.getGenericReturnType()"
                +"\nFor example ReflectionUtil.getGeneticTypeClass(someReflectField.getGenericType())");
    }

    private static Class getClassFromParametrizedTypeArgument(ParameterizedType type) {
        Type[] actualTypeArguments = type.getActualTypeArguments();
        if(actualTypeArguments.length > 1) {
            throw new IllegalArgumentException("Error can not determine actual element type. Number of type should by 1 is " + actualTypeArguments.length);
        }
        return (Class)actualTypeArguments[0];
    }
}
