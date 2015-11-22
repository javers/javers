package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use @ShallowReference to mark certain references
 * to be threat by JaVers as ValueObjects
 * (would stop building object graph from it).
 * <br/><br/>
 *
 * @author akrystian
 */

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ShallowReference {
}
