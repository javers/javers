package org.javers.core.diff.appenders;

import java.util.Optional;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ReferenceAddedChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ReferenceRemovedChange;
import org.javers.core.diff.changetype.ReferenceUpdatedChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.MissingProperty;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;

import java.util.Objects;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
class ReferenceChangeAppender implements PropertyChangeAppender<ReferenceChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ManagedType;
    }

    @Override
    public ReferenceChange calculateChanges(NodePair pair, JaversProperty property) {
        if(pair.getLeftPropertyValue(property) instanceof MissingProperty){
            return new ReferenceAddedChange(pair.getGlobalId(), property.getName(), Optional.empty(), pair.getRightReference(property), pair.getRight());
        } else if(pair.getRightPropertyValue(property) instanceof MissingProperty) {
            return new ReferenceRemovedChange(pair.getGlobalId(), property.getName(), Optional.empty(), pair.getLeftReference(property), pair.getLeft());
        }

        GlobalId leftId = pair.getLeftReference(property);
        GlobalId rightId = pair.getRightReference(property);

        if (Objects.equals(leftId, rightId)) {
            return null;
        }

        return new ReferenceUpdatedChange(pair.getGlobalId(), property.getName(), leftId, rightId,
            pair.getLeftPropertyValue(property), pair.getRightPropertyValue(property), Optional.empty());
    }
}
