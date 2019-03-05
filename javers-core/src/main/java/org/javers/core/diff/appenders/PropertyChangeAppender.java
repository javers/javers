package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import java.util.Optional;

/**
 * Property-scope comparator,
 * follows Chain-of-responsibility pattern.
 * <br/><br/>
 *
 * Implementation should calculate diff between two property values
 *
 * @author bartosz walacik
 */
public interface PropertyChangeAppender<T extends PropertyChange> {
    int HIGH_PRIORITY = 1;
    int LOW_PRIORITY = 2;

    /**
     * Checks if given property type is supported
     */
    boolean supports(JaversType propertyType);

    T calculateChanges(NodePair pair, JaversProperty property);

    default int priority() {
        return LOW_PRIORITY;
    }
}
