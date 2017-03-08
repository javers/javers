package org.javers.core.graph;

import org.javers.core.diff.ObjectGraph;
import org.javers.core.metamodel.object.CdoWrapper;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public class LiveGraph extends ObjectGraph<CdoWrapper> {
    private final ObjectNode root;

    LiveGraph(ObjectNode root, Set<ObjectNode> nodes) {
        super(nodes);
        this.root = root;
    }

    public ObjectNode root() {
        return root;
    }
}
