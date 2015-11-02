package org.javers.common.reflection;

import org.javers.common.collections.Primitives;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class ReflectionUtil {

    public static boolean isJava8runtime(){
        return isClassPresent("java.time.LocalDate");
    }

    public static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, Javers.class.getClassLoader());
            return true;
        }
        catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }

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

    public static List<JaversField> getAllPersistentFields(Class methodSource) {
        List<JaversField> result = new ArrayList<>();
        for(JaversField field : getAllFields(methodSource)) {
            if (isPersistentField(field.getRawMember())) {
                result.add(field);
            }
        }
        return result;
    }

    public static List<JaversMethod> findAllPersistentGetters(Class methodSource) {
        List<JaversMethod> result = new ArrayList<>();
        for(JaversMethod m : getAllMethods(methodSource)) {
             if (isPersistentGetter(m.getRawMember())) {
                 result.add(m);
             }
        }
        return result;
    }

    /**
     * @see JaversMethodFactory#getAllMethods()
     */
    public static List<JaversMethod> getAllMethods(Class methodSource) {
        JaversMethodFactory methodFactory = new JaversMethodFactory(methodSource);
        return methodFactory.getAllMethods();
    }

    public static List<JaversField> getAllFields(Class<?> methodSource) {
        JaversFieldFactory fieldFactory = new JaversFieldFactory(methodSource);
        return fieldFactory.getAllFields();
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

    public static boolean isGetter(Method m) {
        return (m.getName().startsWith("get") ||
                m.getName().startsWith("is")) &&
                m.getParameterTypes().length == 0;
    }

    private static boolean isPrivate(Member member){
        return Modifier.isPrivate(member.getModifiers());
    }

    /**
     * Makes sense only for {@link ParameterizedType}
     */
    public static List<Type> extractActualClassTypeArguments(Type javaType) {
        if (!(javaType instanceof ParameterizedType)) {
            return Collections.emptyList();
        }

        ParameterizedType parameterizedType = (ParameterizedType)javaType;

        List<Type> result = new ArrayList<>();
        for (Type t : parameterizedType.getActualTypeArguments() ) {

            if (t instanceof Class || t instanceof ParameterizedType) {
                result.add(t);
            } else if (t instanceof WildcardType) {
                // If the wildcard type has an explicit upper bound (i.e. not Object), we use that
                WildcardType wildcardType = (WildcardType) t;
                if (wildcardType.getLowerBounds().length == 0) {
                    for (Type type : wildcardType.getUpperBounds()) {
                        if (type instanceof Class && ((Class<?>) type).equals(Object.class)) {
                            continue;
                        }
                        result.add(type);
                    }
                }
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

    public static String reflectiveToString(Object cdoId) {
        if (cdoId == null){
            return "";
        }

        if (cdoId instanceof String) {
            return (String) cdoId;
        }

        if (Primitives.isPrimitiveOrBox(cdoId)){
            return cdoId.toString();
        }

        StringBuilder ret = new StringBuilder();
        for (JaversField f : getAllPersistentFields(cdoId.getClass()) ){
            ret.append( f.invokeEvenIfPrivate(cdoId).toString() );
            ret.append(",");
        }

        if (ret.length() == 0) {
            return cdoId.toString();
        }
        else{
            ret.delete(ret.length()-1, ret.length());
            return ret.toString();
        }
    }
}
