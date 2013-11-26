package org.javers.core.diff.appenders;

import static java.util.Map.Entry;

import java.util.*;

import org.javers.core.diff.calculators.MultiEdgeDifferenceCalculator;
import org.javers.core.diff.visitors.EdgeProjectingVisitor;
import org.javers.model.domain.Change;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.object.graph.*;

/**
 * @author Maciej Zasada
 */
@Deprecated
public class PropertyChangeSetAppender  {

    private MultiEdgeDifferenceCalculator multiEdgeDifferenceCalculator;

    public PropertyChangeSetAppender(MultiEdgeDifferenceCalculator multiEdgeDifferenceCalculator) {
        this.multiEdgeDifferenceCalculator = multiEdgeDifferenceCalculator;
    }

    protected Set<Change> getChangeSet(Set<ObjectNode> leftGraph, Set<ObjectNode> rightGraph) {
        Set<Change> changeSet = new HashSet<>();
        Map<GlobalCdoId, ObjectNode> leftHashTable = buildMapFrom(leftGraph);
        Map<GlobalCdoId, ObjectNode> rightHashTable = buildMapFrom(rightGraph);

        for (Entry<GlobalCdoId, ObjectNode> nodeEntry : rightHashTable.entrySet()) {
            if (leftHashTable.containsKey(nodeEntry.getKey())) {
                changeSet.addAll(calculateObjectNodeDifference(nodeEntry.getValue(), leftHashTable.get(nodeEntry.getKey())));
            }
        }

        return changeSet;
    }

    private Set<Change> calculateObjectNodeDifference(ObjectNode left, ObjectNode right) {
        Set<Change> changeSet = new HashSet<>();
        EdgeProjectingVisitor projectedEdges = new EdgeProjectingVisitor().visit(left.getEdges(), right.getEdges());
        for (Entry<MultiEdge, MultiEdge> multiEdgeProjection : projectedEdges.getMultiEdgesProjection().entrySet()) {
            changeSet.addAll(
                    multiEdgeDifferenceCalculator.calculateMultiEdgeDifference(
                            multiEdgeProjection.getKey(),
                            multiEdgeProjection.getValue(),
                            right.getGlobalCdoId()));
        }

        return changeSet;
    }

    private Map<GlobalCdoId, ObjectNode> buildMapFrom(Set<ObjectNode> graph) {
        Map<GlobalCdoId, ObjectNode> map = new HashMap<>();
        for (ObjectNode objectNode : graph) {
            map.put(objectNode.getGlobalCdoId(), objectNode);
        }
        return map;
    }
}
