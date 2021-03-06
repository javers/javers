package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.*;

import java.util.Objects;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
class ReferenceChangeAppender implements PropertyChangeAppender<ReferenceChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ManagedType && ! (propertyType instanceof ValueObjectType);
    }

    @Override
    public ReferenceChange calculateChanges(NodePair pair, JaversProperty property) {
        GlobalId leftId = pair.getLeftReference(property);
        GlobalId rightId = pair.getRightReference(property);

        if (Objects.equals(leftId, rightId)) {
            return null;
        }

        return new ReferenceChange(pair.createPropertyChangeMetadata(property), leftId, rightId,
                pair.getLeftPropertyValue(property),
                pair.getRightPropertyValue(property));
    }
}
