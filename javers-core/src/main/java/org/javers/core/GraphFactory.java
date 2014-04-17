package org.javers.core;

import org.javers.common.collections.Optional;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.snapshot.GraphShadowFactory;
import org.javers.core.snapshot.GraphSnapshotFactory;
import org.javers.core.snapshot.ShadowGraph;

import java.util.List;

/**
 * Facade to all graph factories
 *
 * @author bartosz walacik
 */
public class GraphFactory {
    private final GraphSnapshotFactory graphSnapshotFactory;
    private final GraphShadowFactory graphShadowFactory;
    private final LiveGraphFactory liveGraphFactory;

    public GraphFactory(GraphSnapshotFactory graphSnapshotFactory, GraphShadowFactory graphShadowFactory, LiveGraphFactory liveGraphFactory) {
        this.graphSnapshotFactory = graphSnapshotFactory;
        this.graphShadowFactory = graphShadowFactory;
        this.liveGraphFactory = liveGraphFactory;
    }

    public LiveGraph createLiveGraph(Object currentVersion) {
        return liveGraphFactory.createLiveGraph(currentVersion);
    }

    public ShadowGraph createLatestShadow(LiveGraph liveGraph){
        return graphShadowFactory.createLatestShadow(liveGraph);
    }

    //capture current state
    public List<CdoSnapshot> createGraphSnapshot(LiveGraph currentVersion){
        return graphSnapshotFactory.create(currentVersion);
    }
}
