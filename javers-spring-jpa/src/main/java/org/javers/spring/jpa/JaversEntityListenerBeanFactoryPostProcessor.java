package org.javers.spring.jpa;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;
import static org.springframework.beans.factory.BeanFactoryUtils.transformedBeanName;
import static org.springframework.context.annotation.aspectj.SpringConfiguredConfiguration.BEAN_CONFIGURER_ASPECT_BEAN_NAME;
import static org.springframework.data.jpa.util.BeanDefinitionUtils.getBeanDefinition;
import static org.springframework.util.StringUtils.addStringToArray;

/**
 * {@link BeanFactoryPostProcessor} that ensures that the {@link AnnotationBeanConfigurerAspect} aspect is up and
 * running <em>before</em> the {@link javax.persistence.EntityManagerFactory} gets created as this already instantiates
 * entity listeners and we need to get injection into {@link org.springframework.beans.factory.annotation.Configurable}
 * to work in them.
 */
class JaversEntityListenerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

        try {
            getBeanDefinition(BEAN_CONFIGURER_ASPECT_BEAN_NAME, beanFactory);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException(
                    "Aspect for @Configured is not present, make sure to enable @EnableJaversEntityListeners", e);
        }

        for (String beanName : getEntityManagerFactoryBeanNames(beanFactory)) {
            BeanDefinition definition = getBeanDefinition(beanName, beanFactory);
            definition.setDependsOn(addStringToArray(definition.getDependsOn(), BEAN_CONFIGURER_ASPECT_BEAN_NAME));
        }
    }

    private static Iterable<String> getEntityManagerFactoryBeanNames(ListableBeanFactory beanFactory) {
        String[] beanNames = beanNamesForTypeIncludingAncestors(beanFactory, EntityManagerFactory.class, true, false);
        Set<String> names = new HashSet<>(asList(beanNames));

        for (String factoryBeanName : beanNamesForTypeIncludingAncestors(beanFactory,
                AbstractEntityManagerFactoryBean.class, true, false)) {
            names.add(transformedBeanName(factoryBeanName));
        }

        return names;
    }
}
