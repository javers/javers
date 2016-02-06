package org.javers.core.metamodel.annotation;

import org.javers.core.JaversBuilder;
import org.javers.core.MappingStyle;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use DiffIgnore annotation to mark certain properties
 * or classes as ignored by JaVers.
 *
 * <H2>Property level</H2>
 * Add DiffIgnore to fields or getters
 * (depending on selected {@link org.javers.core.MappingStyle})
 * to mark them as ignored.
 * When used on property level,
 * DiffIgnore is equivalent to the javax.persistence.Transient annotation.
 *
 * <H2>Class level</H2>
 * Add DiffIgnore to classes to mark them as ignored. <br/>
 * When certain class is ignored, all properties
 * (found in other classes) with this class as a return type are ignored.
 *
 * @see JaversBuilder#withMappingStyle(MappingStyle)
 * @author bartosz walacik
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface DiffIgnore {
}
