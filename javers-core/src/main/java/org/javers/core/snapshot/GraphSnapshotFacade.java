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
    private final GraphShadowFactory graphShadowFactory;

    public GraphSnapshotFacade(GraphSnapshotFactory graphSnapshotFactory, GraphShadowFactory graphShadowFactory) {
        this.graphSnapshotFactory = graphSnapshotFactory;
        this.graphShadowFactory = graphShadowFactory;
    }

    public ShadowGraph createLatestShadow(LiveGraph currentGraph) {
        return graphShadowFactory.createLatestShadow(currentGraph);
    }

    public List<CdoSnapshot> createGraphSnapshot(LiveGraph currentGraph, ShadowGraph latestShadowGraph, CommitMetadata commitMetadata) {
        return graphSnapshotFactory.create(currentGraph, latestShadowGraph, commitMetadata);
    }
}
