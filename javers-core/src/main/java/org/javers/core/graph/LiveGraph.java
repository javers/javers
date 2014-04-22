package org.javers.core.graph;

import org.javers.core.diff.DFSGraphToSetConverter;
import org.javers.core.diff.ObjectGraph;
import org.javers.core.graph.ObjectNode;

import java.util.Collections;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class LiveGraph implements ObjectGraph {
    private final ObjectNode root;
    private final DFSGraphToSetConverter graphToSetConverter = new DFSGraphToSetConverter();
    private final Set<ObjectNode> nodeSet;

    LiveGraph(ObjectNode root) {
        this.root = root;
        this.nodeSet = graphToSetConverter.convertFromGraph(root);
    }

    @Override
    public Set<ObjectNode> flatten() {
        return Collections.unmodifiableSet(nodeSet);
    }
}
