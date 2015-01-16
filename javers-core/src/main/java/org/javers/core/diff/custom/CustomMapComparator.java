package org.javers.core.diff.custom;

import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

/**
 * Useful for classes that are logically maps but do not implement a java.util.Map interface.
 * <br/><br/>
 *
 * Implementation should calculate diff between two maps of type T
 * and return the result as {@link MapChange}.
 * <br/>
 * For example, T could be a Multimap from Guava.
 *
 * @author bartosz walacik
 */
public interface CustomMapComparator<T> extends CustomPropertyComparator<T, MapChange>{

    @Override
    MapChange compare(T left, T right, GlobalId affectedId, Property property);
}
