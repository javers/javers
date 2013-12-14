package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.changeType.ReferenceChanged;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.object.graph.Edge;
import org.javers.model.object.graph.ObjectNode;
import org.javers.model.object.graph.SingleEdge;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
public class ReferenceChangeAppender extends PropertyChangeAppender<ReferenceChanged> {

    @Override
    protected Set<Class<JaversType>> getSupportedPropertyTypes() {
        return ENTITY_REF_TYPES;
    }

    @Override
    public Collection<ReferenceChanged> calculateChanges(NodePair pair, Property property) {
        ObjectNode left = pair.getLeft();
        ObjectNode right =pair.getRight();

        GlobalCdoId leftGlobalCdoId = ((SingleEdge) left.getEdge(property)).getReference().getGlobalCdoId();
        GlobalCdoId rightGlobalCdoId = ((SingleEdge) right.getEdge(property)).getReference().getGlobalCdoId();

        if (leftGlobalCdoId.equals(rightGlobalCdoId)) {
            return Collections.EMPTY_SET;
        }

        return Sets.asSet(new ReferenceChanged(pair.getGlobalCdoId(),
                property,
                leftGlobalCdoId,
                rightGlobalCdoId));
    }
}
