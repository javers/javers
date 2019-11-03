package org.javers.core.metamodel.annotation;

import org.javers.core.metamodel.type.EntityType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Maps a user's class to {@link EntityType}
 *
 * @author bartosz walacik
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {
}
