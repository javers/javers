package org.javers.core.snapshot;

import org.javers.core.diff.ObjectGraph;
import org.javers.core.graph.ObjectNode;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public class ShadowGraph implements ObjectGraph {
    private final Set<ObjectNode> snapshots;

    ShadowGraph(Set<ObjectNode> snapshots) {
        this.snapshots = snapshots;
    }

    @Override
    public Set<ObjectNode> nodes() {
        return snapshots;
    }

    @Override
    public ObjectNode root() {
        throw new RuntimeException("not implemented");
    }
}
