package org.javers.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use <code>ShallowReference</code> to mark certain Entities as compared only by Id.
 * <br/><br/>
 *
 * When <code>ShallowReference</code> is enabled for a given Entity,
 * all its properties (except idProperty) are ignored.
 * <br/><br/>
 *
 * <code>ShallowReference</code> annotation can be used both globally, on the class-level, for example:
 *
 * <pre>
 * &#64;ShallowReference
 * class Entity {
 *     &#64;Id String id;
 *     ...
 *  }
 * </pre>
 *
 * and locally, on the property-level, for example:
 *
 * <pre>
 * class Entity {
 *     &#64;Id String id;
 *     ...
 *  }
 *
 *  class AnotherEntity {
 *       &#64;Id String id;
 *       &#64;ShallowReference Entity shallowReference;
 *       &#64;ShallowReference List&lt;Entity&gt; shallowReferences;
 *       Entity regularReference;
 *       ...
 *  }
 * </pre>
 *
 * @author akrystian
 */

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface ShallowReference {
}
