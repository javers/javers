package org.javers.core.diff.appenders;

import org.javers.core.diff.changetype.map.MapChange;

/**
 * Custom Map comparator.
 * <br/>
 * Useful for classes that are logically maps but do not implement java.util.Map interface.
 * <br/><br/>
 *
 * Implementation should calculate diff between two maps of type T
 * and return the result as MapChange.
 * For example, T could be a Multimap from Guava.
 *
 * @author bartosz walacik
 */
public interface CustomMapComparator<T> extends CustomPropertyComparator<T, MapChange>{

    MapChange compare(T left, T right);
}
