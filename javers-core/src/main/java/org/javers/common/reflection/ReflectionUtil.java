package org.javers.common.reflection;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.common.validation.Validate;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author bartosz walacik
 */
public class ReflectionUtil {
    public static final String TRANSIENT_ANN = "Transient";
    public static final String ID_ANN = "Id";

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
     * list all class methods, including inherited and private,
     * removes inheritance duplicates
     */
    public static List<Method> getAllMethods(Class methodSource){
        List<Method> methods = new ArrayList<>();
        Set<Integer> added = new HashSet<>();

        Class clazz = methodSource;
        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                int methodKey = methodKey(m);
                if (added.contains(methodKey)) {
                    // System.out.println("filtered inheritance duplicate" +m);
                    continue;
                }
                methods.add(m);
                added.add(methodKey);
            }
            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    private static int methodKey(Method m){
        int key = shaDigest(m.getName());
        for (Class c : m.getParameterTypes()) {
            key += c.hashCode();
        }
        return key;
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

        return  !isAnnotationPresent(m, TRANSIENT_ANN) &&
                !Modifier.isStatic(m.getModifiers())  &&
                !Modifier.isAbstract(m.getModifiers())  &&
                !Modifier.isNative(m.getModifiers()) ;
    }

    public static boolean isAnnotationPresent(Method method, String annotationName){
        Validate.argumentsAreNotNull(method, annotationName);

        if (contains(method.getAnnotations(), annotationName)) {
            return true;
        }

        return false;
    }

    public static boolean isAnnotationPresent(Field field, String annotationName){
        Validate.argumentsAreNotNull(field, annotationName);

        if (contains(field.getDeclaredAnnotations(), annotationName)) {
            return true;
        }

        return false;
    }

    private static boolean contains(Annotation[] annotations, String annotationName) {
        for (Annotation a : annotations){
            if (a.annotationType().getSimpleName().equals(annotationName)){
                return true;
            }
        }
        return false;
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

    /**
     * for example: Map<String, String> -> Map
     */
    public static Class extractClass(Type javaType) {
        if (javaType instanceof ParameterizedType &&
                ((ParameterizedType)javaType).getRawType() instanceof Class){
            return (Class)((ParameterizedType)javaType).getRawType();
        }  else if (javaType instanceof Class) {
            return (Class)javaType;
        }

        throw new JaversException(JaversExceptionCode.CLASS_EXTRACTION_ERROR, javaType);
    }

    /**
     *
     */
    public static int calculateHierarchyDistance(Class<?> clazz, Class<?> parent) {
        Class<?> current = clazz;
        int distance = 0;

        //search in class hierarchy
        while (current != null) {
            //try class
            if (parent == current) {
                return distance;
            }

            //try interfaces
            for (Class<?> interf : current.getInterfaces()) {
                if (parent == interf) {
                    return distance + 1;
                }
            }

            //step up in class hierarchy
            current = current.getSuperclass();

            distance++;
        }

        return Integer.MAX_VALUE;
    }

    private static int shaDigest(String text){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(text.getBytes("UTF-8"));
            byte[] hashBytes = digest.digest();

            int result = 0;
            for (int i=0; i<hashBytes.length; i++){
                result += Math.abs(hashBytes[i]) * (i+1);
            }
            return result;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
