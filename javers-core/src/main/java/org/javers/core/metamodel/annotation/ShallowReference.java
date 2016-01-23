package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use ShallowReference to mark certain Entities as compared only by Id.
 * <br/><br/>
 *
 * When ShallowReference is enabled for given Entity,
 * all its properties except idProperty are ignored.
 * <br/>
 * It stops building Object Graph from objects of this Entity.
 *
 * @author akrystian
 */

@Target(TYPE)
@Retention(RUNTIME)
public @interface ShallowReference {
}
