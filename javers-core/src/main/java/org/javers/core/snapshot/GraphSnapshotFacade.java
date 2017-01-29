package org.javers.core.snapshot;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.LiveGraph;
import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class GraphSnapshotFacade {
    private final GraphSnapshotFactory graphSnapshotFactory;
    private final ShadowGraphFactory shadowGraphFactory;

    public GraphSnapshotFacade(GraphSnapshotFactory graphSnapshotFactory, ShadowGraphFactory shadowGraphFactory) {
        this.graphSnapshotFactory = graphSnapshotFactory;
        this.shadowGraphFactory = shadowGraphFactory;
    }

    public ShadowGraph createLatestShadow(LiveGraph currentGraph) {
        return shadowGraphFactory.createLatestShadow(currentGraph);
    }

    public List<CdoSnapshot> createGraphSnapshot(LiveGraph currentGraph, ShadowGraph latestShadowGraph, CommitMetadata commitMetadata) {
        return graphSnapshotFactory.create(currentGraph, latestShadowGraph, commitMetadata);
    }
}
