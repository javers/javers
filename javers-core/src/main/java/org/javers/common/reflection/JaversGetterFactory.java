package org.javers.common.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author bartosz walacik
 */
class JaversGetterFactory {
    private final Class getterSource;

    JaversGetterFactory(Class getterSource) {
        this.getterSource = getterSource;
    }

    /**
     * List all class getters, including inherited and private.
     */
    List<JaversGetter> getAllGetters(){
        List<JaversGetter> getters = new ArrayList<>();
        TypeResolvingContext context = new TypeResolvingContext();

        Class clazz = getterSource;
        while (clazz != Object.class) {
            context.addTypeSubstitutions(clazz);
            Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> isGetter(method) && !method.isBridge())
                    .map(getterMethod -> createJaversGetter(getterMethod, context))
                    .forEach(getters::add);
            clazz = clazz.getSuperclass();
        }

        return getters;
    }

    private static boolean isGetter(Method rawMethod) {
        return hasGetOrIsPrefix(rawMethod) &&
                hasNoParamters(rawMethod) &&
                returnsSomething(rawMethod) &&
                isNotStatic(rawMethod) &&
                isNotAbstract(rawMethod) &&
                isNotNative(rawMethod);
    }

    private static boolean hasGetOrIsPrefix(Method rawMethod) {
        return rawMethod.getName().startsWith("get") ||
                rawMethod.getName().startsWith("is");
    }

    private static boolean hasNoParamters(Method rawMethod) {
        return rawMethod.getParameterTypes().length == 0;
    }

    private static boolean returnsSomething(Method rawMethod) {
        return rawMethod.getGenericReturnType() != void.class;
    }

    private static boolean isNotStatic(Method rawMethod) {
        return !Modifier.isStatic(rawMethod.getModifiers());
    }

    private static boolean isNotAbstract(Method rawMethod) {
        return !Modifier.isAbstract(rawMethod.getModifiers());
    }

    private static boolean isNotNative(Method rawMethod) {
        return !Modifier.isNative(rawMethod.getModifiers());
    }

    private JaversGetter createJaversGetter(Method getterMethod, TypeResolvingContext context){
        Type actualReturnType = context.getSubstitution(getterMethod.getGenericReturnType());
        return new JaversGetter(getterMethod, actualReturnType);
    }
}
