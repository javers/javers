package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.SnapshotFactory;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.api.JaversExtendedRepository;

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
    private final JaversExtendedRepository javersRepository;

    GraphSnapshotFactory(SnapshotFactory snapshotFactory, JaversExtendedRepository javersRepository) {
        this.snapshotFactory = snapshotFactory;
        this.javersRepository = javersRepository;
    }

    /**
     * @param currentVersion outcome from {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    List<CdoSnapshot> create(LiveGraph currentVersion, ShadowGraph latestShadowGraph, CommitMetadata commitMetadata){
        Validate.argumentsAreNotNull(currentVersion, commitMetadata, latestShadowGraph);

        List<CdoSnapshot> result = new ArrayList<>();

        for (ObjectNode node : currentVersion.nodes()) {
            boolean initial = isInitial(node, latestShadowGraph);

            CdoSnapshot fresh = createFreshSnapshot(initial, node, commitMetadata);

            Optional<CdoSnapshot> existing = javersRepository.getLatest(fresh.getGlobalId());

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

    private CdoSnapshot createFreshSnapshot(boolean initial, ObjectNode node, CommitMetadata commitMetadata){
        if (initial){
            return snapshotFactory.createInitial(node, commitMetadata);
        }
        else{
            return snapshotFactory.create(node, commitMetadata);
        }
    }

    List<CdoSnapshot> create(LiveGraph currentVersion, CommitMetadata commitMetadata) {
        return create(currentVersion, ShadowGraph.EMPTY, commitMetadata);
    }

    private boolean isInitial(ObjectNode node, ShadowGraph latestShadowGraph){
        return !latestShadowGraph.nodes().contains(node);
    }
}
