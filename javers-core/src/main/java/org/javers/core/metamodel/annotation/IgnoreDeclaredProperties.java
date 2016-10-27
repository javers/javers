package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 * Add {@link IgnoreDeclaredProperties} to classes to ignore all their properties.
 * <br>
 * <br>
 * If a class is annotated with {@link IgnoreDeclaredProperties} and is part of an audited hierarchy,
 * only the properties of the class are ignored, and JaVers will still track any other properties in the
 * class hierarchy.
 * By contrast, if a class is annotated with {@link DiffIgnore} JaVers will ignore all instances of
 * that class.
 * @see DiffIgnore
 * @author Edward Mallia
 */
@Target({ TYPE})
@Retention(RUNTIME)
public @interface IgnoreDeclaredProperties
{
}
