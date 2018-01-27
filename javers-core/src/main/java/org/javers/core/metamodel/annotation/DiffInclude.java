package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use DiffInclude annotation to tell JaVers what properties to include in the diff results for this type.
 * All other properties in the current type and it's subclasses will be ignored by JaVers.
 *
 * If more or different properties should be included in a subclass, apply the annotation to the subclass with the different list of properties.
 * <br/><br/>
 * For example, in the below example, JaVers will ignore every property except for id and foo:
 * <pre>
 * class A {
 *     &#64;Id
 *     &#64;DiffInclude
 *     private Long id;
 *
 *     &#64;DiffInclude
 *     private String foo;
 *
 *     private String bar;
 * }
 *
 * Suppose we have a subclass
 * class B extends A {
 *     private String qux;
 * }
 * </pre>
 *
 *
 * The above is equivalent to:
 * <pre>
 * class A {
 *     &#64;Id
 *     private Long id;
 *
 *     private String foo;
 *
 *     &#64;DiffIgnore
 *     private String bar;
 * }
 *
 * and
 *
 * class B extends A {
 *     &#64;DiffIgnore
 *     private String qux;
 * }
 *
 * @see DiffIgnore
 * @author Iulian Stefanica
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface DiffInclude {
}
