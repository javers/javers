package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use EntityStringId annotation to define toString method to be call to compare properties.
 *
 * @author ismael costa
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface EntityStringId {
}
