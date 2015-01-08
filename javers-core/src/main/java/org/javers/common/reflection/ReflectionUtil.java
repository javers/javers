package org.javers.common.reflection;

import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
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
    public static final String ID_ANN = "Id";

    private static final Object[] EMPTY_ARRAY = new Object[]{};

    /**
     * Creates new instance of public or package-private class.
     * Calls first, not-private constructor
     */
    public static Object newInstance(Class clazz, ArgumentResolver resolver){
        Validate.argumentIsNotNull(clazz);
        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            if (isPrivate(constructor)) {
                continue;
            }

            Class [] types = constructor.getParameterTypes();
            Object[] params = new Object[types.length];
            for (int i=0; i<types.length; i++){
                params[i] = resolver.resolve(types[i]);
            }
            try {
                constructor.setAccessible(true);
                return constructor.newInstance(params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new JaversException(JaversExceptionCode.NO_PUBLIC_CONSTRUCTOR,clazz.getName());
    }

    public static List<Field> getAllPersistentFields(Class methodSource) {
        List<Field> result = new ArrayList<>();
        for(Field field : getAllFields(methodSource)) {
            if (isPersistentField(field)) {
                result.add(field);
            }
        }
        return result;
    }

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

    public static List<Field> getAllFields(Class<?> methodSource) {
        List<Field> fields =  Lists.asList(methodSource.getDeclaredFields());

        Class superclass = methodSource.getSuperclass();
        if (superclass != null && superclass != Object.class) { //recursion stop condition
            fields.addAll( getAllFields(methodSource.getSuperclass()) );
        }
        return fields;
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
     * </ul>
     */
    public static boolean isPersistentGetter(Method m) {
        if (!isGetter(m)){
            return false;
        }

        return  !Modifier.isStatic(m.getModifiers()) &&
                !Modifier.isAbstract(m.getModifiers()) &&
                !Modifier.isNative(m.getModifiers()) ;
    }

    public static boolean isPersistentField(Field field) {

        return !Modifier.isTransient(field.getModifiers()) &&
               !Modifier.isStatic(field.getModifiers()) &&
               !field.getName().equals("this$0"); //owner of inner class
    }

    public static boolean isAnnotationPresent(AccessibleObject methodOrField, String annotationName){
        Validate.argumentsAreNotNull(methodOrField, annotationName);

        if (contains(methodOrField.getAnnotations(), annotationName)) {
            return true;
        }

        return false;
    }

    public static boolean hasAnyAnnotation(AccessibleObject methodOrField, Set<String> annotationNames){
        for (String annotationName : annotationNames){
            if (isAnnotationPresent(methodOrField, annotationName)) {
                return true;
            }
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
        setAccessibleIfNecessary(getter);
        return invokeGetter(getter, onObject);
    }

    public static Object invokeFieldEvenIfPrivate(Field field, Object onObject) {
        setAccessibleIfNecessary(field);
        return invokeField(field, onObject);
    }

    public static Object invokeField(Field field, Object onObject) {

        try {
            return field.get(onObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("error getting value from field '"+ field.getName() +"'",e);
        }
    }
    
    private static void setAccessibleIfNecessary(Member member) {
        if(!isPublic(member))
        {
            ((AccessibleObject)member).setAccessible(true); //that's Java Reflection API ...
        }
    }

    private static boolean isPublic(Member member){
        return Modifier.isPublic(member.getModifiers());
    }

    private static boolean isPrivate(Member member){
        return Modifier.isPrivate(member.getModifiers());
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
