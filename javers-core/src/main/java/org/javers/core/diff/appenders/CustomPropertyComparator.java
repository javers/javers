package org.javers.core.diff.appenders;

import org.javers.core.diff.changetype.PropertyChange;

/**
 * Custom property-scope change appender.
 * <br/><br/>
 *
 * Implementation should calculate diff between two property values of type T.
 *
 * @author bartosz walacik
 */
public interface CustomPropertyComparator<T, C extends PropertyChange> {
    C compare(T left, T right);
}
