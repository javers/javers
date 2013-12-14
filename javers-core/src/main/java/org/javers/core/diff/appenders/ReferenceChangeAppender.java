package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.changeType.ReferenceChanged;
import org.javers.model.mapping.Entity;
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

        Object leftReference = left.getPropertyValue(property);
        Object rightReference = right.getPropertyValue(property);

        if (leftReference == rightReference) {
            return Collections.EMPTY_SET;
        }

        GlobalCdoId leftEntity = gerReferencedGlobalCdoId(left, property);
        GlobalCdoId rightEntity = gerReferencedGlobalCdoId(right, property);

        return Sets.asSet(new ReferenceChanged(pair.getGlobalCdoId(),
                property,
                leftEntity,
                rightEntity));
    }


    private GlobalCdoId gerReferencedGlobalCdoId(ObjectNode node, Property property) {
        for (Edge edge : node.getEdges()) {
            if (edge.getProperty().equals(property)) {
                SingleEdge singleEdge =  (SingleEdge) edge;
                return singleEdge.getReference().getGlobalCdoId();
            }
        }

        return null;
    }
}
