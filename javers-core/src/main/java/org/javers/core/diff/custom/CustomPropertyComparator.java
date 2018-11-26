package org.javers.core.diff.custom;

import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.Objects;
import java.util.Optional;

/**
 * Custom property-scope comparator.
 * Typically, Custom Types are large structures (like Multimap) or complex objects,
 * for simple values use {@link CustomValueComparator}.
 * <br/><br/>
 *
 * Implementation should calculate a diff between two objects of given Custom Type.
 * <br/><br/>
 *
 * For example, if you are using Multimap from Guava,
 * implement:
 * <pre>
 * {@code public class GuavaCustomComparator implements CustomPropertyComparator<Multimap> {
 *     public MapChange compare(Multimap left, Multimap right, GlobalId affectedId, Property property) {
 *       ... // omitted
 *     }
 *   }
 * }</pre>
 *
 * and register a custom comparator in JaversBuilder:
 * <pre>
 * JaversBuilder.javers().registerCustomComparator(new GuavaCustomComparator(), Multimap.class).build()
 * </pre>
 *
 * @param <T> Custom Type
 * @param <C> Concrete type of PropertyChange returned by a comparator
 * @author bartosz walacik
 */
public interface CustomPropertyComparator<T, C extends PropertyChange> {
    /**
     * Calculates changes between two Custom Type objects.
     * <br/>
     * This comparator is called by JaVers to calculate property-to-property diff.
     *
     * @param left left (or old) value
     * @param right right (or current) value
     * @param affectedId Id of domain object being compared
     * @param property property being compared
     *
     * @return should return Optional.empty() if compared objects are the same
     */
    Optional<C> compare(T left, T right, GlobalId affectedId, Property property);

    /**
     * This comparator is called by JaVers to calculate collection-to-collection diff,
     * when Custom Type objects are Collection items.
     * <br/><br/>
     *
     * Both equals() and compare() should return consistent results. When compare() returns null,
     * equals() should return false.
     *
     */
    default boolean equals(T a, T b) {
        return Objects.equals(a, b);
    }
}
