package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;

/**
 * Property-scope change appender,
 * follows Chain-of-responsibility pattern
 *
 * @author bartosz walacik
 */
public interface PropertyChangeAppender <T extends PropertyChange> {
    /**
     * checks if given property is supported and
     */
    boolean supports(JaversType propertyType);

    T calculateChanges(NodePair pair, Property supportedProperty);
}
