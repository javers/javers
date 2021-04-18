package org.javers.spring.annotation;

import org.javers.core.Javers;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on a conditional deleting method (typically on a Repository method)
 * <br/><br/>
 *
 * Triggers {@link Javers#commitShallowDelete} for each returned entity.
 * <br/><br/>
 *
 * Usage:
 *
 * <pre>
 *    {@literal @}JaversAuditableDelete
 *     List<DummyEntity> deleteByName(String name) {
 *         ...
 *     }
 * </pre>
 * @see JaversAuditableAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JaversAuditableConditionalDelete {

}
