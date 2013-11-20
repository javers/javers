package org.javers.core.diff;

import org.javers.model.object.graph.*;
import org.javers.model.visitors.Visitable;
import org.javers.model.visitors.Visitor;

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

    private class DFSGraphTraverser implements Visitor {

        private final Set<ObjectNode> visitedNodes = new HashSet<>();

        private void visitNode(ObjectNode objectNode) {
            visitedNodes.add(objectNode);
            for (Edge edge : objectNode.getEdges()) {
                edge.accept(this);
            }
        }

        private Set<ObjectNode> getVisitedNodes() {
            return visitedNodes;
        }

        @Override
        public void visit(Object object) {

            if(object instanceof ObjectNode) {
                visitedNodes.add((ObjectNode) object);
            }
        }



    }
}
