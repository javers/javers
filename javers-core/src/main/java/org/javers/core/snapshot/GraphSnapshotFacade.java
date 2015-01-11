package org.javers.core.snapshot;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.graph.LiveGraph;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalIdDTO;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class GraphSnapshotFacade {
    private final SnapshotDiffer snapshotDiffer;
    private final GraphSnapshotFactory graphSnapshotFactory;
    private final GraphShadowFactory graphShadowFactory;

    public GraphSnapshotFacade(SnapshotDiffer snapshotDiffer, GraphSnapshotFactory graphSnapshotFactory, GraphShadowFactory graphShadowFactory) {
        this.snapshotDiffer = snapshotDiffer;
        this.graphSnapshotFactory = graphSnapshotFactory;
        this.graphShadowFactory = graphShadowFactory;
    }

    public List<Change> getChangeHistory(GlobalIdDTO globalId, int limit) {
        return snapshotDiffer.getChangeHistory(globalId, limit);
    }


    public ShadowGraph createLatestShadow(LiveGraph currentGraph) {
        return graphShadowFactory.createLatestShadow(currentGraph);
    }

    public List<CdoSnapshot> createGraphSnapshot(LiveGraph currentGraph, ShadowGraph latestShadowGraph, CommitMetadata commitMetadata) {
        return graphSnapshotFactory.create(currentGraph, latestShadowGraph, commitMetadata);
    }
}
