package org.javers.core.diff;

import org.javers.model.object.graph.Edge;
import org.javers.model.object.graph.MultiEdge;
import org.javers.model.object.graph.ObjectNode;
import org.javers.model.object.graph.SingleEdge;
import org.javers.model.object.graph.EdgeVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Maciej Zasada
 */
public class DFSGraphToSetConverter implements GraphToSetConverter {

    @Override
    public Set<ObjectNode> convertFromGraph(ObjectNode graph) {
        DFSGraphTraverser dfsGraphTraverser = new DFSGraphTraverser();
        dfsGraphTraverser.visitNode(graph);
        return dfsGraphTraverser.getVisitedNodes();
    }

    private class DFSGraphTraverser implements EdgeVisitor {

        private final Set<ObjectNode> visitedNodes = new HashSet<>();

        @Override
        public void visit(SingleEdge edge) {
            visitNode(edge.getReference());
        }

        @Override
        public void visit(MultiEdge edge) {
            for (ObjectNode objectNode : edge.getReferences()) {
                visitNode(objectNode);
            }
        }

        private void visitNode(ObjectNode objectNode) {
            visitedNodes.add(objectNode);
            for (Edge edge : objectNode.getEdges()) {
                edge.accept(this);
            }
        }

        private Set<ObjectNode> getVisitedNodes() {
            return visitedNodes;
        }
    }
}
