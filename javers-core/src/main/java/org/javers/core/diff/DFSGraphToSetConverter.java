package org.javers.core.diff;

import org.javers.model.object.graph.*;

import java.util.Set;

/**
 * Flattens given graph into set using Depth-first search approach.
 *
 * @author Maciej Zasada
 */
public class DFSGraphToSetConverter  {

    public Set<ObjectNode> convertFromGraph(ObjectNode graph) {
        CollectNodesVisitor visitor = new CollectNodesVisitor();
        graph.accept(visitor);
        return visitor.getNodes();
    }
}
