package org.javers.spring.annotation;

import org.javers.core.Javers;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auto-audit when put on a conditionally deleting method (typically on a Repository method).
 * <br/><br/>
 *
 * The annotated method should return a deleted Entity or a collection of deleted Entities.
 * <br/><br/>
 *
 * Triggers {@link Javers#commitShallowDelete(String, Object)} on each returned Entity.
 * <br/><br/>
 *
 * Usage:
 *
 * <pre>
 * {@literal @}JaversAuditableConditionalDelete
 *  List&lt;DummyEntity&gt; deleteByName(String name) {
 *     ...
 *  }
 * </pre>
 *
 * or <br/><br/>
 *
 * <pre>
 *{@literal @}JaversAuditableConditionalDelete
 * DummyEntity deleteById(String id) {
 *    ...
 * }
 * </pre>
 *
 * @see JaversAuditableAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JaversAuditableConditionalDelete {

}
