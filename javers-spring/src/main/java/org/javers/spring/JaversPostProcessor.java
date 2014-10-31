package org.javers.spring;

import org.javers.core.Javers;
import org.javers.spring.aspect.SimpleDynamicPointcat;
import org.javers.spring.aspect.JaversAdvice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class JaversPostProcessor implements BeanPostProcessor {

    Javers javers;

    public JaversPostProcessor(Javers javers) {
        this.javers = javers;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        ProxyFactory pf = new ProxyFactory();

        boolean found = false;

        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(JaversAudit.class)) {
                found = true;
                Advisor advisor = new DefaultPointcutAdvisor(new SimpleDynamicPointcat(method), new JaversAdvice(javers));
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
