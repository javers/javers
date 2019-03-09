package org.javers.core.snapshot;

import org.javers.core.graph.ObjectGraph;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.Set;

/**
 * @author bartosz walacik
 */
class SnapshotGraph extends ObjectGraph<CdoSnapshot> {
    SnapshotGraph(Set<SnapshotNode> snapshots) {
        super((Set)snapshots);
    }
}
