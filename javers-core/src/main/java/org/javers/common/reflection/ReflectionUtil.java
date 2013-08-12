package org.javers.common.reflection;

import javax.persistence.Transient;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class ReflectionUtil {

    public static List<Method> findAllPublicPersistentGetters(Class methodSource) {
        List<Method> result = new ArrayList<>();
        for(Method m : methodSource.getMethods()) {
             if (isPersistentGetter(m)) {
                 result.add(m);
             }
        }

        return result;
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
}
