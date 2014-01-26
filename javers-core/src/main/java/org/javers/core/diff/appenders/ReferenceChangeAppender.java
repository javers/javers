package org.javers.core.diff.appenders;

import org.javers.common.collections.Objects;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.model.domain.GlobalCdoId;
import org.javers.core.metamodel.property.Property;
import org.javers.model.mapping.type.EntityReferenceType;
import org.javers.model.mapping.type.JaversType;

import java.util.Collection;
import java.util.Collections;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
public class ReferenceChangeAppender extends PropertyChangeAppender<ReferenceChange> {

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return EntityReferenceType.class;
    }

    @Override
    public Collection<ReferenceChange> calculateChanges(NodePair pair, Property property) {
        GlobalCdoId leftGlobalCdoId = pair.getLeftGlobalCdoId(property);
        GlobalCdoId rightGlobalCdoId = pair.getRightGlobalCdoId(property);

        if (Objects.nullSafeEquals(leftGlobalCdoId, rightGlobalCdoId)) {
            return Collections.EMPTY_SET;
        }

        return Collections.singleton(
                        new ReferenceChange(pair.getGlobalCdoId(),
                        property,
                        leftGlobalCdoId,
                        rightGlobalCdoId));
    }
}
