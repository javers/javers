package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use ShallowReference to mark certain Entities as compared only by Id.
 * <br/><br/>
 *
 * When ShallowReference is enabled for a given Entity,
 * all its properties (except idProperty) are ignored.
 * <br/>
 * JaVers stops building Object Graph from ShallowReference Entities.
 *
 * @author akrystian
 */

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface ShallowReference {
}
