package org.javers.core.metamodel.annotation;

import org.javers.core.MappingStyle;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use the {@code DiffIgnoreProperties} annotation to mark a list properties (fields or methods)
 * of a class as ignored by Javers.
 * <br/><br/>
 *
 * All properties listed in the annotation are ignored by the Javers.
 * <br/>
 * Using this annotation is equiv to putting {@link DiffIgnore} on each of the listed property.
 * <br/><br/>
 *
 * Usage with {@link MappingStyle#FIELD}:
 * <pre>
 * &#64;DiffIgnoreProperties("field1", "field2")
 * public class MyClass {
 *     String field1;
 *     String field2;
 *     String field3;
 * }
 * </pre>
 *
 * Usage with {@link MappingStyle#BEAN}:
 * <pre>
 * &#64;DiffIgnoreProperties("getName1", "getName2")
 * public class MyClass {
 *     String getName1() { ... }
 *     String getName2() { ... }
 *     String getName3() { ... }
 * }
 * </pre>
 *
 * @author Edgars Garsneks
 * @see DiffIgnore
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface DiffIgnoreProperties {

    String[] value() default {};

}
