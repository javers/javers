package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;

import java.util.Objects;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
class ReferenceChangeAppender extends CorePropertyChangeAppender<ReferenceChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ManagedType;
    }

    @Override
    public ReferenceChange calculateChanges(NodePair pair, JaversProperty property) {
        GlobalId leftId = pair.getLeftGlobalId(property);
        GlobalId rightId = pair.getRightGlobalId(property);

        if (Objects.equals(leftId, rightId)) {
            return null;
        }

        return new ReferenceChange(pair.getGlobalId(), property.getName(), leftId, rightId,
            pair.getLeftPropertyValue(property), pair.getRightPropertyValue(property));
    }
}
