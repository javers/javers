package org.javers.core.diff;

import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.object.graph.Fake;
import org.javers.model.object.graph.ObjectNode;

import java.util.*;

import static org.javers.common.collections.Sets.difference;

/**
 * @author bartosz walacik
 */
public class NodeMatcher {
    /**
     * matching based on {@link org.javers.model.domain.GlobalCdoId}
     */
    public List<NodePair> match(Set<ObjectNode> leftGraph, Set<ObjectNode> rightGraph) {
        Validate.argumentsAreNotNull(leftGraph,rightGraph);

        List<NodePair> pairs = new ArrayList<>();
        Map<GlobalCdoId, ObjectNode> rightMap = asMap(rightGraph);

        Set<ObjectNode> temp = new HashSet<>();

        for (ObjectNode left : leftGraph) {
            GlobalCdoId key = left.getGlobalCdoId();
            if (rightMap.containsKey(key)) {
                pairs.add(new NodePair(left,rightMap.get(key)));
                temp.add(rightMap.get(key));
            }
        }

        for (ObjectNode node : difference(rightGraph, leftGraph)) {
            Fake fake = new Fake(node.getGlobalCdoId());
            pairs.add(new NodePair(fake, node));
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
