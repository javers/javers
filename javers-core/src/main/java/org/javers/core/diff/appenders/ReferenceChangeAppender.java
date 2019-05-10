package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ReferenceAddedChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ReferenceRemovedChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.MissingProperty;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;

import java.util.Objects;
import java.util.Optional;

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
        GlobalId leftId = pair.getLeftReference(property);
        GlobalId rightId = pair.getRightReference(property);

        if (Objects.equals(leftId, rightId)) {
            return null;
        }

        return ReferenceChange.create(pair.getGlobalId(), property.getName(), leftId, rightId, pair.getLeftPropertyValue(property), pair.getRightPropertyValue(property));
    }
}
