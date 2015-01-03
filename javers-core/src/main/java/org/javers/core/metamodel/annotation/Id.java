package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use @Id to map Entity unique identifier (field or getter)
 *
 * @author bartosz walacik
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Id {
}
