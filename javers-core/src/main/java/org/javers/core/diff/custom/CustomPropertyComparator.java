package org.javers.core.diff.custom;

import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

/**
 * Custom property-scope comparator.
 * <br/><br/>
 *
 * Implementation should calculate a diff between two Value types.
 * <br/><br/>
 *
 * For example, if you are using Multimap from Guava,
 * implement:
 * <pre>
 * {@code public class GuavaCustomComparator implements CustomPropertyComparator<Multimap, MapChange> {
 *     public MapChange compare(Multimap left, Multimap right, GlobalId affectedId, Property property) {
 *       ... // omitted
 *     }
 *   }
 * }</pre>
 *
 * and register a custom comparator in JaversBuilder:
 * <pre>
 * JaversBuilder.javers()
 *     .registerCustomComparator(new GuavaCustomComparator(), Multimap.class).build()
 * </pre>
 *
 * @param <T> custom type, e.g. Multimap
 * @param <C> concrete type of PropertyChange returned by a comparator
 * @author bartosz walacik
 */
public interface CustomPropertyComparator<T, C extends PropertyChange> {
    /**
     * @param left left (or old) property value
     * @param right right (or current) property value
     * @param affectedId Id of domain object being compared
     * @param property property being compared
     * @return should return null if compared objects have no differences
     */
    C compare(T left, T right, GlobalId affectedId, Property property);
}
