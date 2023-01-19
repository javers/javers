package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use {@code DiffIgnoreFields} annotation to mark certain properties as ignored by JaVers.
 * <br><br>
 * <p>
 * Add {@code DiffIgnoreFields} to a class and list property names in the annotation value.
 * All property names listed in the annotation will be ignored by the JaVers.
 *
 * @author Edgars Garsneks
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface DiffIgnoreFields {

    String[] value() default {};

}
