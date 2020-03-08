package org.javers.spring.annotation;

import org.javers.core.Javers;
import org.javers.spring.auditable.aspect.JaversAuditableAspectAsync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Executor;

/**
 * <b>INCUBATING - Javers Async API has incubating status.</b>
 * <br/><br/>
 *
 * Enables asynchronous auto-audit aspect when put on a method (typically in a Repository).
 * <br/><br/>
 *
 * Triggers {@link Javers#commitAsync(String, Object, Executor)} for each method argument.
 * <br/><br/>
 *
 * <b>Important!</b> Works with MongoDB, not implemented for SQL repositories.
 *
 * @see JaversAuditableAspectAsync
 * @author Razi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JaversAuditableAsync {
}
