package org.javers.spring.annotation;

import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.springframework.data.repository.CrudRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables Javers auto-audit aspect when put on Spring Data {@link CrudRepository}
 *
 * @see JaversSpringDataAuditableRepositoryAspect
 * @author Florian Gessner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JaversSpringDataAuditable {
}
