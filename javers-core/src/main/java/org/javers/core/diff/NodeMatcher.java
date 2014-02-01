package org.javers.core.diff;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.model.object.graph.ObjectNode;

import java.util.*;

/**
 * @author bartosz walacik
 */
public class NodeMatcher {
    /**
     * matching based on {@link org.javers.core.metamodel.object.GlobalCdoId}
     */
    public List<NodePair> match(GraphPair graphPair) {
        Validate.argumentIsNotNull(graphPair);

        List<NodePair> pairs = new ArrayList<>();
        Map<GlobalCdoId, ObjectNode> rightMap = asMap(graphPair.getRightNodeSet());

        for (ObjectNode left : graphPair.getLeftNodeSet()) {
            GlobalCdoId key = left.getGlobalCdoId();
            if (rightMap.containsKey(key)) {
                pairs.add(new RealNodePair(left,rightMap.get(key)));
            }
        }

        return pairs;
    }

    private Map<GlobalCdoId, ObjectNode> asMap(Set<ObjectNode> nodes) {
        Map<GlobalCdoId, ObjectNode> map = new HashMap<>();

        for (ObjectNode node : nodes) {
            map.put(node.getGlobalCdoId(),node);
        }

        return map;
    }
}
