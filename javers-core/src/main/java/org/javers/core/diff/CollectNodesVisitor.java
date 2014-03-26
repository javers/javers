package org.javers.core.diff;

import org.javers.core.graph.GraphVisitor;
import org.javers.core.graph.ObjectNode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class CollectNodesVisitor extends GraphVisitor {
    private final Set<ObjectNode> nodes = new HashSet<>();

    @Override
    public void visitOnce(ObjectNode node) {
        nodes.add(node);
    }

    public Set<ObjectNode> getNodes() {
        return nodes;
    }
}
