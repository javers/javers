package org.javers.core.diff.custom;

import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.CustomType;

import java.util.Optional;

/**
 * Property-scope comparator bounded to {@link CustomType}.
 * <br/><br/>
 *
 * Typically, Custom Types are large structures (like Multimap) or complex objects.
 * For simple values, it's better to use {@link CustomValueComparator}.
 * <br/><br/>
 *
 * Implementation should calculate a diff between two objects of given Custom Type.
 * <br/><br/>
 *
 * Examples and doc:
 * <a href="https://javers.org/documentation/diff-configuration/#custom-comparators">https://javers.org/documentation/diff-configuration/#custom-comparators</a>
 * <br/><br/>
 *
 * Usage:
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
     * Called by JaVers to calculate property-to-property diff
     * between two Custom Type objects.
     *
     * @param left left (or old) value
     * @param right right (or current) value
     * @param metadata call {@link PropertyChangeMetadata#getAffectedCdoId()} to get
     *                 Id of domain object being compared
     * @param property property being compared
     *
     * @return should return Optional.empty() if compared objects are the same
     */
    Optional<C> compare(T left, T right, PropertyChangeMetadata metadata, Property property);

    /**
     * Called by JaVers to calculate collection-to-collection diff,
     * when Custom Type objects are Collection items.
     * <br/><br/>
     *
     * Both equals() and compare() should return consistent results. When compare() returns
     * Optional.empty(), equals() should return false.
     */
    boolean equals(T a, T b);
}
