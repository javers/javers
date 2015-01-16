package org.javers.core.diff.custom;

import org.javers.core.diff.changetype.map.MapChange;

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

    MapChange compare(T leftMap, T rightMap);
}
