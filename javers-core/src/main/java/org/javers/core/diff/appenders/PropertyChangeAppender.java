package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;

/**
 * Property scope change appender,
 * follows Chain-of-responsibility pattern
 *
 * @author bartosz walacik
 */
public abstract class PropertyChangeAppender <T extends PropertyChange> {
    /**
     * checks if given property is supported and if so,
     * delegates calculation to concrete appender in calculateChanges()
     *
     * @return null if no changes
     */
    public final T calculateChangesIfSupported(NodePair pair, Property property, JaversType propertyType) {
        if (!supports(propertyType)) {
             return null;
        }
        return calculateChanges(pair, property);
    }

    protected abstract boolean supports(JaversType propertyType);

    protected abstract T calculateChanges(NodePair pair, Property supportedProperty);
}
