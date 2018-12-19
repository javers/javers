package org.javers.spring.annotation;

import org.javers.spring.auditable.aspect.JaversAuditableDeleteAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on a delete method (typically on a method in a Repository)
 *
 * @see JaversAuditableDeleteAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JaversAuditableDelete {
}