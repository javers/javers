package org.javers.core.diff.calculators;

import org.javers.common.collections.Sets;
import org.javers.model.domain.Change;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.changeType.ReferenceAdded;
import org.javers.model.object.graph.MultiEdge;
import org.javers.model.object.graph.ObjectNode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Maciej Zasada
 */
public class MultiEdgeDifferenceCalculator {

    public Set<Change> calculateMultiEdgeDifference(MultiEdge left, MultiEdge right, GlobalCdoId ownerNodeCdoId) {
        Set<Change> changeSet = new HashSet<>();
        changeSet.addAll(findAddedReferences(left, right, ownerNodeCdoId));
        return changeSet;
    }

    public Set<Change> findAddedReferences(MultiEdge left, MultiEdge right, GlobalCdoId ownerNodeCdoId) {
        Set<Change> changeSet = new HashSet<>();
        Set<ObjectNode> addedReferences = Sets.difference(Sets.asSet(right.getReferences()), Sets.asSet(left.getReferences()));
        for (ObjectNode addedReference : addedReferences) {
            changeSet.add(new ReferenceAdded(ownerNodeCdoId, right.getProperty(), addedReference.getGlobalCdoId()));
        }
        return changeSet;
    }

}
