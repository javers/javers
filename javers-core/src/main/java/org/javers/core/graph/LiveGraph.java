package org.javers.core.graph;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public class LiveGraph extends ObjectGraph<LiveCdo> {
    private final ObjectNode root;

    LiveGraph(ObjectNode root, Set<ObjectNode<LiveCdo>> nodes) {
        super(nodes);
        this.root = root;
    }

    ObjectNode root() {
        return root;
    }
}
