package org.javers.model.object.graph;

import org.javers.common.patterns.visitors.Visitor;
import java.util.HashSet;
import java.util.Set;

/**
 * Ensures that each node is visited only once.
 * Extend it and overwrite {@link #visitOnce(ObjectNode)} to add concrete Visitor behaviour
 *
 * @author bartosz walacik
 */
public class GraphVisitor implements Visitor<ObjectNode> {
    private Set<ObjectNode> visited = new HashSet<>();

    @Override
    public final void visit(ObjectNode node) {
        visitOnce(node);
        markVisited(node);
    }

    /**
     * Overwrite to add concrete Visitor behaviour
     */
    public void visitOnce(ObjectNode node) {
    }

    public boolean wasVisited(ObjectNode node) {
        return visited.contains(node);
    }

    private void markVisited(ObjectNode node) {
        visited.add(node);
    }
}
