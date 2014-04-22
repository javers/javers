package org.javers.core.diff.appenders;

import org.javers.common.collections.Objects;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;

import java.util.Collection;
import java.util.Collections;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
public class ReferenceChangeAppender extends PropertyChangeAppender<ReferenceChange> {

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return ManagedType.class;
    }

    @Override
    public ReferenceChange calculateChanges(NodePair pair, Property property) {
        GlobalCdoId leftGlobalCdoId = pair.getLeftGlobalCdoId(property);
        GlobalCdoId rightGlobalCdoId = pair.getRightGlobalCdoId(property);

        if (Objects.nullSafeEquals(leftGlobalCdoId, rightGlobalCdoId)) {
            return null;
        }

        return new ReferenceChange(pair.getGlobalCdoId(), property, leftGlobalCdoId, rightGlobalCdoId);
    }
}
