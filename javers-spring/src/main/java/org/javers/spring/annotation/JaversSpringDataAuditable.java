package org.javers.spring.annotation;

import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on Spring Data CrudRepositories
 *
 * @see JaversSpringDataAuditableRepositoryAspect
 * @author Florian Gessner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JaversSpringDataAuditable {
}
