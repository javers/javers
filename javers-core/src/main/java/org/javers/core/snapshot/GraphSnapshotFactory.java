package org.javers.core.snapshot;

import java.util.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.ObjectGraph;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Decomposes given live object graph into a flat list of object Snapshots.
 * Resulting structure can be easily serialized and persisted.
 *
 * @author bartosz walacik
 */
class GraphSnapshotFactory {

    private final SnapshotFactory snapshotFactory;

    GraphSnapshotFactory(SnapshotFactory snapshotFactory) {
        this.snapshotFactory = snapshotFactory;
    }

    /**
     * @param currentVersion outcome from ObjectGraphBuilder.buildGraph()
     */
    List<CdoSnapshot> create(LiveGraph currentVersion, ObjectGraph<CdoSnapshot> latestShadowGraph, CommitMetadata commitMetadata){
        Validate.argumentsAreNotNull(currentVersion, commitMetadata, latestShadowGraph);

        List<CdoSnapshot> result = new ArrayList<>();

        for (ObjectNode node : currentVersion.nodes()) {
            boolean initial = isInitial(node, latestShadowGraph);

            Optional<CdoSnapshot> existing = latestShadowGraph.get(node.getGlobalId());

            CdoSnapshot fresh = createFreshSnapshot(initial, node, commitMetadata, existing);

            if (!existing.isPresent()) {
                result.add(fresh); //when insert
                continue;
            }

            if (!existing.get().stateEquals(fresh)) {
                result.add(fresh); //when update
            }

            //when not changed
        }

        return result;
    }

    private CdoSnapshot createFreshSnapshot(boolean initial, ObjectNode node, CommitMetadata commitMetadata, Optional<CdoSnapshot> previous){
        CdoWrapper cdoWrapper = (CdoWrapper)node.getCdo();
        if (initial){
            return snapshotFactory.createInitial(cdoWrapper, commitMetadata);
        }
        else{
            return snapshotFactory.createUpdate(cdoWrapper, previous.get(), commitMetadata);
        }
    }

    private boolean isInitial(ObjectNode node, ObjectGraph latestShadowGraph){
        return !latestShadowGraph.contains(node);
    }
}
