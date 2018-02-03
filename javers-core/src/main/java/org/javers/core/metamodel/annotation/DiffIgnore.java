package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use {@code DiffIgnore} annotation to mark certain properties
 * or classes as ignored by JaVers.
 *
 * <H2>Property level</H2>
 * Add {@code DiffIgnore} to fields or getters
 * (depending on selected {@link org.javers.core.MappingStyle})
 * to mark them as ignored.
 * When used on the property level,
 * {@code DiffIgnore} is equivalent to the {@code javax.persistence.Transient} annotation.
 *
 * <H2>Class level</H2>
 * Add {@code DiffIgnore} to classes to mark them as ignored. <br/>
 * When a given class is ignored, all properties
 * (found in other classes) with this class as a return type are ignored.
 *
 * <br/><br/>
 * <b>Warning</b>: {@code DiffIgnore} can't be mixed with {@code DiffInclude} in the same class.
 *
 * @see DiffInclude
 * @author bartosz walacik
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface DiffIgnore {
}
