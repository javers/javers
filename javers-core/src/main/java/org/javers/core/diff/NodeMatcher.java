package org.javers.core.diff;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import java.util.*;

/**
 * @author bartosz walacik
 */
class NodeMatcher {
    /**
     * matching based on {@link org.javers.core.metamodel.object.GlobalId}
     */
    static List<NodePair> match(Set<ObjectNode> leftNodes, Set<ObjectNode> rightNodes, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(leftNodes, rightNodes, commitMetadata);

        List<NodePair> pairs = new ArrayList<>();
        Map<GlobalId, ObjectNode> rightMap = asMap(rightNodes);

        for (ObjectNode left : leftNodes) {
            GlobalId key = left.getGlobalId();
            if (rightMap.containsKey(key)) {
                pairs.add(new NodePair(left, rightMap.get(key), commitMetadata));
            }
        }

        return pairs;
    }

    private static Map<GlobalId, ObjectNode> asMap(Set<ObjectNode> nodes) {
        Map<GlobalId, ObjectNode> map = new HashMap<>();

        for (ObjectNode node : nodes) {
            map.put(node.getGlobalId(),node);
        }

        return map;
    }
}
