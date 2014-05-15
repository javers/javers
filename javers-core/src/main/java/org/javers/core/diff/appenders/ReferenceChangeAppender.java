package org.javers.core.diff.appenders;

import org.javers.common.collections.Objects;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
public class ReferenceChangeAppender extends PropertyChangeAppender<ReferenceChange> {

    @Override
    protected boolean supports(JaversType propertyType) {
        return propertyType instanceof ManagedType;
    }

    @Override
    public ReferenceChange calculateChanges(NodePair pair, Property property) {
        GlobalCdoId leftId =  pair.getLeftGlobalCdoId(property);
        GlobalCdoId rightId = pair.getRightGlobalCdoId(property);

        if (Objects.nullSafeEquals(leftId, rightId)) {
            return null;
        }

        return new ReferenceChange(pair.getGlobalCdoId(), property, leftId, rightId);
    }
}
