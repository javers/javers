package org.javers.spring.jpa;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable support for Javers JPA entity listeners.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({JaversEntityListenerBeanFactoryPostProcessor.class})
@EnableSpringConfigured
public @interface EnableJaversEntityListeners {
}
