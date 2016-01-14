package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use ShallowReference to mark certain classes to be
 * compare only by {@link Id} (would stop building object graph from it).
 * <br/><br/>
 *
 * @author akrystian
 */

@Target(TYPE)
@Retention(RUNTIME)
public @interface ShallowReference {
}
