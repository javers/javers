package org.javers.common.reflection;

import org.javers.common.string.ShaDigest;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author bartosz walacik
 */
class JaversMethodFactory {
    private final Class methodSource;

    public JaversMethodFactory(Class methodSource) {
        this.methodSource = methodSource;
    }

    /**
     * List all class methods, including inherited and private.
     * Inheritance duplicates cleared
     */
    public List<JaversMethod> getAllMethods(){
        List<JaversMethod> methods = new ArrayList<>();
        Set<Integer> added = new HashSet<>();
        TypeResolvingContext context = new TypeResolvingContext();

        Class clazz = methodSource;
        while (clazz != null) {
            context.addTypeSubstitutions(clazz);
            for (Method m : clazz.getDeclaredMethods()) {
                int methodKey = methodKey(m);
                if (added.contains(methodKey)) {
                    // System.out.println("filtered inheritance duplicate" +m);
                    continue;
                }
                methods.add(createJMethod(m, context));
                added.add(methodKey);
            }
            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    private JaversMethod createJMethod(Method rawMethod, TypeResolvingContext context){
        Type actualReturnType = context.getSubstitution(rawMethod.getGenericReturnType());
        return new JaversMethod(rawMethod, actualReturnType);
    }

    public static int methodKey(Method m){
        int key = ShaDigest.shortDigest(m.getName());
        for (Class c : m.getParameterTypes()) {
            key += c.hashCode();
        }
        return key;
    }
}
