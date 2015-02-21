package org.javers.spring.data;

import org.javers.core.Javers;
import org.javers.spring.AuthorProvider;
import org.javers.spring.DefaultAuthorProvider;
import org.javers.spring.aspect.MethodEqualsBasedMatcher;
import org.javers.spring.data.advice.AuditMethodInvocationHandlerFactory;
import org.javers.spring.data.advice.JaversSpringDataAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

import java.lang.reflect.Method;

public class JaversSpringDataPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaversSpringDataPostProcessor.class);

    private final Javers javers;
    private final AuthorProvider authorProvider;

    public JaversSpringDataPostProcessor(Javers javers) {
        this(javers, new DefaultAuthorProvider());
    }

    public JaversSpringDataPostProcessor(Javers javers, AuthorProvider authorProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (isAuditableRepository(bean)) {
            RepositoryMetadata metadata = DefaultRepositoryMetadata.getMetadata(bean.getClass());
            if(metadata != null){
                return proxy(bean, metadata);
            }else{
                LOGGER.warn("Class {} marked implemented and marked as spring repository but not available in registry. The repository will be ignored for auditing", bean.getClass().getName());
            }
        }
        return bean;
    }

    private boolean isAuditableRepository(Object bean) {
        return bean instanceof CrudRepository
                && bean.getClass().isAnnotationPresent(
                JaversSpringDataAuditable.class)
                && !bean.getClass().isAnnotationPresent(NoRepositoryBean.class);
    }

    private Object proxy(Object bean, RepositoryMetadata metadata) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(bean);
        addAdvisors(bean, proxyFactory, metadata);
        return proxyFactory;
    }

    private void addAdvisors(Object bean, ProxyFactory proxyFactory, RepositoryMetadata metadata) {
        JaversSpringDataAdvice advice = new JaversSpringDataAdvice(
                new AuditMethodInvocationHandlerFactory(javers, authorProvider, metadata));
        Class<?> beanClass = bean.getClass();
        addAdvisorsFor(proxyFactory, beanClass, advice);
    }

    private void addAdvisorsFor(ProxyFactory factory, Class<?> beanClass,
                                JaversSpringDataAdvice advice) {
        for (Method method : beanClass.getMethods()) {
            if (isAuditableMethodFor(method)) {
                factory.addAdvisor(new DefaultPointcutAdvisor(
                        new MethodEqualsBasedMatcher(method), advice));
            }
        }
    }

    private boolean isAuditableMethodFor(Method method) {
        return JaversSpringDataAuditEvent
                .isRelevantMethodName(method.getName());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

}
