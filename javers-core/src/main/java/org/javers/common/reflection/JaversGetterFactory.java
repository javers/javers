package org.javers.common.reflection;

import java.lang.reflect.Method;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author bartosz walacik
 */
class JaversGetterFactory {
    private final Class getterSource;
    private final List<JaversGetter> getters = new ArrayList<>();
    private final TypeResolvingContext context = new TypeResolvingContext();

    JaversGetterFactory(Class getterSource) {
        this.getterSource = getterSource;
    }

    /**
     * List all class getters, including inherited and private.
     */
    List<JaversGetter> getAllGetters() {
        if (getters.size() > 0) {
            throw new IllegalStateException("getters.size() > 0");
        }
        findAllGetters(getterSource);

        return getters;
    }

    private void findAllGetters(Class currentGetterSource) {
        Class clazz = currentGetterSource;
        while (clazz != null && clazz != Object.class) {
            context.addTypeSubstitutions(clazz);
            Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> isGetter(method) && !method.isBridge())
                    .filter(method -> !isOverridden(method, getters))
                    .map(getter -> createJaversGetter(getter, context))
                    .forEach(getters::add);

            Arrays.stream(clazz.getInterfaces()).forEach(this::findAllGetters);

            clazz = clazz.getSuperclass();
        }
    }

    private static boolean isGetter(Method rawMethod) {
        return hasGetOrIsPrefix(rawMethod) &&
               hasNoParamters(rawMethod) &&
               returnsSomething(rawMethod) &&
               isNotStatic(rawMethod) &&
               //isNotAbstract(rawMethod) &&
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

    private static boolean isOverridden(Method parent, Collection<JaversGetter> toCheck) {
        return toCheck.stream()
                .map(it -> it.getRawMember())
                .anyMatch(it -> isOverridden(parent, it));
    }

    private static boolean isOverridden(Method parent, Method toCheck) {
        return isSubClass(parent, toCheck) &&
               sameMethodName(parent, toCheck) &&
               returnTypeCovariant(parent, toCheck) &&
               sameArguments(parent, toCheck);
    }

    private static boolean isSubClass(final Method parent, final Method toCheck) {
        return parent.getDeclaringClass().isAssignableFrom(toCheck.getDeclaringClass());
    }

    private static boolean sameMethodName(final Method parent, final Method toCheck) {
        return parent.getName().equals(toCheck.getName());
    }

    private static boolean returnTypeCovariant(final Method parent, final Method toCheck) {
        return parent.getReturnType().isAssignableFrom(toCheck.getReturnType());
    }

    private static boolean sameArguments(final Method parent, final Method toCheck) {
        return Arrays.equals(parent.getParameterTypes(), toCheck.getParameterTypes());
    }

    private JaversGetter createJaversGetter(Method getterMethod, TypeResolvingContext context) {
        Type actualReturnType = context.getSubstitution(getterMethod.getGenericReturnType());

        if (hasInheritedId(getterMethod)) {
            return new JaversGetter(getterMethod, actualReturnType, true);
        }
        return new JaversGetter(getterMethod, actualReturnType);
    }

    private static boolean hasInheritedId(Method concrete) {
        List<Method> overridden = new ArrayList<>();
        Class clazz = concrete.getDeclaringClass().getSuperclass();

        while (clazz != null && clazz != Object.class) {
            Arrays.asList(clazz.getDeclaredMethods())
                    .stream()
                    .filter(parent -> isOverridden(parent, concrete))
                    .findFirst().ifPresent(parent -> overridden.add(parent));
            clazz = clazz.getSuperclass();
        }

        return overridden.stream().anyMatch(ReflectionUtil::looksLikeId);
    }
}
