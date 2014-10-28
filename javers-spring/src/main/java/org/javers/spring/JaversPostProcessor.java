package org.javers.spring;

import org.javers.spring.aspect.DynamicPointcat;
import org.javers.spring.aspect.JaversAdvice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class JaversPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        ProxyFactory pf = new ProxyFactory();

        boolean found = false;

        for (final Method m : bean.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(JaversAudit.class)) {
                found = true;
                Advisor advisor = new DefaultPointcutAdvisor(new DynamicPointcat(m), new JaversAdvice());
                pf.setTarget(bean);
                pf.addAdvisor(advisor);
            }
        }

        if (found) {
            return pf.getProxy();
        } else {
            return bean;
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
