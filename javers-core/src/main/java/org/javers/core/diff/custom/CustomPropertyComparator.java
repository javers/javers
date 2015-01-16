package org.javers.core.diff.custom;

import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

/**
 * Custom property-scope change appender.
 * <br/><br/>
 *
 * Implementation should calculate diff between two property values of type T.
 *
 * @author bartosz walacik
 */
public interface CustomPropertyComparator<T, C extends PropertyChange> {
    /**
     * @param left left (or old) property value
     * @param right right (or current) property value
     * @param affectedId Id of domain object being compared
     * @param property property being compared
     */
    C compare(T left, T right, GlobalId affectedId, Property property);
}
