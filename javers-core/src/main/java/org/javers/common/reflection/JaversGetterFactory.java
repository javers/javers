package org.javers.common.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    List<JaversGetter> getAllGetters() {
        List<JaversGetter> getters = new ArrayList<>();
        TypeResolvingContext context = new TypeResolvingContext();

        Class clazz = getterSource;
        while (clazz != null && clazz != Object.class) {
            context.addTypeSubstitutions(clazz);
            final List<JaversGetter> newProperties = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> isGetter(method) && !method.isBridge())
                    .map(getterMethod -> createJaversGetter(getterMethod, context))
                    .collect(Collectors.toList());
            final List<JaversGetter> overridden = newProperties.stream().flatMap(property ->
                getters.stream().filter((existing) -> isOverridden(property.getRawMember(), existing.getRawMember()))
            ).collect(Collectors.toList());
            getters.removeAll(overridden);
            getters.addAll(newProperties);
            clazz = clazz.getSuperclass();
        }

        return getters;
    }

    private static boolean isGetter(Method rawMethod) {
        return hasGetOrIsPrefix(rawMethod) &&
                hasNoParamters(rawMethod) &&
                returnsSomething(rawMethod) &&
                isNotStatic(rawMethod) &&
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

    private static boolean isNotNative(Method rawMethod) {
        return !Modifier.isNative(rawMethod.getModifiers());
    }

    private static boolean isOverridden(Method parent, Method toCheck) {
        if (parent.getDeclaringClass().isAssignableFrom(toCheck.getDeclaringClass())
                && parent.getName().equals(toCheck.getName())) {
            if (!parent.getReturnType().isAssignableFrom(toCheck.getReturnType())) {
                return false;
            }

            Class<?>[] params1 = parent.getParameterTypes();
            Class<?>[] params2 = toCheck.getParameterTypes();
            if (params1.length == params2.length) {
                for (int i = 0; i < params1.length; i++) {
                    if (!params1[i].equals(params2[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private JaversGetter createJaversGetter(Method getterMethod, TypeResolvingContext context) {
        Type actualReturnType = context.getSubstitution(getterMethod.getGenericReturnType());
        return new JaversGetter(getterMethod, actualReturnType);
    }
}
