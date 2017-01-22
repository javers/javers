package org.javers.core.snapshot;

import java.util.Optional;
import org.javers.core.diff.ObjectGraph;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Collections;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class ShadowGraph implements ObjectGraph {
    private final Set<ObjectNode> snapshots;

    public static ShadowGraph EMPTY = new ShadowGraph(Collections.<ObjectNode>emptySet());

    ShadowGraph(Set<ObjectNode> snapshots) {
        this.snapshots = snapshots;
    }

    @Override
    public Set<ObjectNode> nodes() {
        return Collections.unmodifiableSet(snapshots);
    }

    @Override
    public ObjectNode root() {
        throw new RuntimeException("not implemented");
    }

    boolean contains(ObjectNode node) {
        return snapshots.contains(node);
    }

    Optional<CdoSnapshot> get(GlobalId globalId) {
        for (ObjectNode node : snapshots){
           if (globalId.equals(node.getGlobalId())){
               return Optional.of((CdoSnapshot) node.getCdo());
           }
        }
        return Optional.empty();
    }
}
