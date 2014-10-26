package org.javers.core.metamodel.annotation;

import org.javers.core.metamodel.type.ValueType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Maps user's class to {@link ValueType}
 *
 * @author bartosz walacik
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Value {
}
