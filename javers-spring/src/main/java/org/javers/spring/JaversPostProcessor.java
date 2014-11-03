package org.javers.spring;

import org.javers.common.collections.Sets;
import org.javers.core.Javers;
import org.javers.spring.aspect.SimpleDynamicPointcut;
import org.javers.spring.aspect.JaversAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class JaversPostProcessor implements BeanPostProcessor {

    private final Javers javers;
    private final AuthorProvider authorProvider;

    public JaversPostProcessor(Javers javers) {
        this(javers, new DefaultAuthorProvider());
    }

    public JaversPostProcessor(Javers javers, AuthorProvider authorProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        Set<Method> methods = new HashSet<>();

        if (isAnnotationOverClass(bean)) {
            methods.addAll(Sets.asSet(bean.getClass().getDeclaredMethods()));
        } else {
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(JaversAuditable.class)) {
                    methods.add(method);
                }
            }
        }

        ProxyFactory proxyFactory = proxyFactoryWithAdvisors(bean, methods);

        return proxyFactory.getAdvisors().length > 0 ? proxyFactory.getProxy() : bean;
    }

    private ProxyFactory proxyFactoryWithAdvisors(Object bean, Set<Method> methods) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(bean);
        addAdvisors(proxyFactory, methods);
        return proxyFactory;
    }

    private void addAdvisors(ProxyFactory proxyFactory, Set<Method> methods) {
        for (Method method : methods) {
            proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new SimpleDynamicPointcut(method),
                    new JaversAdvice(javers, authorProvider.provide())));
        }
    }

    private boolean isAnnotationOverClass(Object bean) {
        return bean.getClass().isAnnotationPresent(JaversAuditable.class);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
