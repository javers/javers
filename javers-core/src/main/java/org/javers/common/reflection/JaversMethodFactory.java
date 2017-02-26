package org.javers.common.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
     */
    public List<JaversMethod> getAllMethods(){
        List<JaversMethod> methods = new ArrayList<>();
        TypeResolvingContext context = new TypeResolvingContext();

        Class clazz = methodSource;
        while (clazz != null) {
            context.addTypeSubstitutions(clazz);
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isBridge()) {
                    continue;
                }
                methods.add(createJMethod(m, context));
            }
            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    private JaversMethod createJMethod(Method rawMethod, TypeResolvingContext context){
        Type actualReturnType = context.getSubstitution(rawMethod.getGenericReturnType());
        return new JaversMethod(rawMethod, actualReturnType);
    }
}
