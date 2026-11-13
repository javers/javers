package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks an embeddable as cascading so that the deletion of the containing entity will also mark the embeddable as deleted.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Cascading {

    boolean value() default true;
}
