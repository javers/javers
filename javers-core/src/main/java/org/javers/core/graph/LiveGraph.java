package org.javers.core.graph;

import org.javers.core.diff.ObjectGraph;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public class LiveGraph implements ObjectGraph {
    private final Set<ObjectNode> nodes;
    private final ObjectNode root;

    LiveGraph(ObjectNode root, Set<ObjectNode> nodes) {
        this.nodes = nodes;
        this.root = root;
    }

    @Override
    public ObjectNode root() {
        return root;
    }

    @Override
    public Set<ObjectNode> nodes() {
        return nodes;
    }
}
