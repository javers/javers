package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use @DiffIgnore to mark certain properties
 * (fields or getters) to be ignored by JaVers diff.
 * <br/><br/>
 *
 * @DiffIgnore is an equivalent for javax.persistence.Transient
 *
 * @author bartosz walacik
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface DiffIgnore {
}
