package org.javers.spring.annotation;

import org.javers.core.Javers;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on a deleting method (typically on a Repository method)
 * <br/><br/>
 *
 * Triggers {@link Javers#commitShallowDelete} for each method argument.
 * <br/><br/>
 *
 * Usage:
 *
 * <pre>
 *    {@literal @}JaversAuditableDelete
 *     void delete(DummyEntity entity) {
 *         ...
 *     }
 * </pre>
 *
 * or:
 *
 * <pre>
 *    {@literal @}JaversAuditableDelete(entity = DummyEntity)
 *     void deleteById(String id) {
 *         ...
 *     }
 * </pre>
 * @see JaversAuditableAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JaversAuditableDelete {

    /**
     * Entity class, required only when deleting by id, for example:
     */
    Class<?> entity() default Void.class;
}
