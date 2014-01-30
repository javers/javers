package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.property.Property;
import org.javers.model.mapping.type.*;

import java.util.Collection;
import java.util.Collections;

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
     */
    public final Collection<T> calculateChangesIfSupported(NodePair pair, Property property, JaversType propertyType) {
        if (!supports(propertyType)) {
             return Collections.EMPTY_SET;
        }
        return calculateChanges(pair, property);
    }

    protected boolean supports(JaversType propertyType) {
        return getSupportedPropertyType().isAssignableFrom( propertyType.getClass() );
    }

    protected abstract Class<? extends JaversType> getSupportedPropertyType();

    protected abstract Collection<T> calculateChanges(NodePair pair, Property supportedProperty);
}
