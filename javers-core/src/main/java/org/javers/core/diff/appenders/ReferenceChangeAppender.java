package org.javers.core.diff.appenders;

import org.javers.common.collections.Objects;
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
        Edge leftSingleEdge = pair.getLeft().getEdge(property);
        Edge rightSingleEdge = pair.getRight().getEdge(property);

        GlobalCdoId leftGlobalCdoId = getGlobalCdoId(leftSingleEdge);
        GlobalCdoId rightGlobalCdoId = getGlobalCdoId(rightSingleEdge);

        if (Objects.nullSafeEquals(leftGlobalCdoId, rightGlobalCdoId)) {
            return Collections.EMPTY_SET;
        }

        return Sets.asSet(new ReferenceChanged(pair.getGlobalCdoId(),
                property,
                leftGlobalCdoId,
                rightGlobalCdoId));
    }

    private GlobalCdoId getGlobalCdoId(Edge edge) {
        return edge != null ? ((SingleEdge) edge).getReference().getGlobalCdoId() : null;
    }
}
