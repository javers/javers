package org.javers.spring.annotation;

import org.javers.core.Javers;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on a delete method (typically on a method in a Repository)
 * <br/><br/>
 *
 * Triggers {@link Javers#commitShallowDelete} for each method argument.
 *
 * @see JaversAuditableAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JaversAuditableDelete {

    /**
     * domain type of entity to delete, required in case of delete by id
     */
    Class<?> domainType() default Void.class;
}
