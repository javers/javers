package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use IgnoreDeclaredProperties annotation to mark
 * all properties <b>declared</b> in a given class as ignored by JaVers.
 * <br/><br/>
 *
 * JaVers still tracks instances of a given class and tracks changes done on properties of
 * its superclass
 * (by contrast, if a class is annotated with {@link DiffIgnore}, JaVers completely ignores instances of
 * that class).
 *
 * <br/><br/>
 * For example, when you want to ignore all properties declared in a subclass B but
 * still track changes in properties declared in a superclass A:
 * <pre>
 * class A {
 *     &#64;Id
 *     private Long id;
 *     private String name;
 * }
 * </pre>
 *
 * this mapping:
 * <pre>
 * &#64;IgnoreDeclaredProperties
 * class B extends A {
 *     private String foo;
 *     private String bar;
 * }
 * </pre>
 *
 * is equivalent to:
 * <pre>
 * class B extends A {
 *     &#64;DiffIgnore
 *     private String foo;
 *     &#64;DiffIgnore
 *     private String bar;
 * }
 * </pre>
 *
 * @see DiffIgnore
 * @author Edward Mallia
 */
@Target({ TYPE})
@Retention(RUNTIME)
public @interface IgnoreDeclaredProperties {
}
