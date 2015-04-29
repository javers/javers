package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.SnapshotFactory;

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
     * @param currentVersion outcome from {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    List<CdoSnapshot> create(LiveGraph currentVersion, ShadowGraph latestShadowGraph, CommitMetadata commitMetadata){
        Validate.argumentsAreNotNull(currentVersion, commitMetadata, latestShadowGraph);

        List<CdoSnapshot> result = new ArrayList<>();

        for (ObjectNode node : currentVersion.nodes()) {
            boolean initial = isInitial(node, latestShadowGraph);

           Optional<CdoSnapshot> existing = latestShadowGraph.get(node.getGlobalId());

           CdoSnapshot fresh = createFreshSnapshot(initial, node, commitMetadata, existing);

            if (existing.isEmpty()) {
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
        Object liveCdo = node.wrappedCdo().get();
        if (initial){
            return snapshotFactory.createInitial(liveCdo, node.getGlobalId(), commitMetadata);
        }
        else{
            //we take previous globalId because it could be PersistentGlobalId
            return snapshotFactory.createUpdate(liveCdo, previous.get(), commitMetadata);
        }
    }

    private boolean isInitial(ObjectNode node, ShadowGraph latestShadowGraph){
        return !latestShadowGraph.contains(node);
    }
}
