package org.javers.spring.annotation;

import org.javers.core.Javers;
import org.javers.spring.auditable.aspect.JaversAuditableAspectAsync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on a method (typically on a method in a Repository)
 * <br/><br/>
 *
 * Triggers {@link Javers#commitAsync} for each method argument.
 *
 * @see JaversAuditableAspectAsync
 * @author Razi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JaversAuditableAsync {
}
