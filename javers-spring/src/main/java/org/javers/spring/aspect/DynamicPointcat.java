package org.javers.spring.aspect;

import org.springframework.aop.support.DynamicMethodMatcherPointcut;

import java.lang.reflect.Method;

public class DynamicPointcat extends DynamicMethodMatcherPointcut {
    private final Method m;

    public DynamicPointcat(Method m) {
        this.m = m;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object[] args) {
        return m.equals(method);
    }
}
