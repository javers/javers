package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.OptionalType;

/**
 * @author bartosz.walacik
 */
public class OptionalChangeAppender extends CorePropertyChangeAppender<PropertyChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof OptionalType;
    }

    @Override
    public PropertyChange calculateChanges(NodePair pair, Property supportedProperty) {
        return null;
    }
}
