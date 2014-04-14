package org.javers.core;

import org.javers.common.collections.Optional;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.snapshot.GraphShadowFactory;
import org.javers.core.snapshot.GraphSnapshotFactory;

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

    public ObjectNode createLiveGraph(Object currentVersion) {
        return liveGraphFactory.createLiveGraph(currentVersion);
    }

    public Optional<ObjectNode> createLatestShadow(Object currentVersion){
        return graphShadowFactory.createLatestShadow(currentVersion);
    }

    //capture current state
    public List<CdoSnapshot> createGraphSnapshot(ObjectNode currentVersion){
        return graphSnapshotFactory.create(currentVersion);
    }
}
