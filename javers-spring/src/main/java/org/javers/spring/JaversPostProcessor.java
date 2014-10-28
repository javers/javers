package org.javers.spring;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class JaversPostProcessor implements BeanPostProcessor {

    private AspectFactory factory;

    public JaversPostProcessor(AspectFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(JaversAudit.class)) {
            AspectJProxyFactory proxyFactory = new AspectJProxyFactory(bean);

            for (Object aspect : factory.create()) {
                proxyFactory.addAspect(aspect);
            }

            return proxyFactory.getProxy();
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
