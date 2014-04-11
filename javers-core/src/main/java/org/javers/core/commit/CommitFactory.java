package org.javers.core.commit;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.snapshot.GraphShadowFactory;
import org.javers.core.snapshot.GraphSnapshotFactory;
import org.javers.repository.api.JaversRepository;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private final GraphSnapshotFactory graphSnapshotFactory;
    private final GraphShadowFactory graphShadowFactory;
    private final LiveGraphFactory liveGraphFactory;
    private final DiffFactory diffFactory;

    public CommitFactory(GraphSnapshotFactory graphSnapshotFactory, GraphShadowFactory graphShadowFactory, LiveGraphFactory liveGraphFactory, DiffFactory diffFactory) {
        this.graphSnapshotFactory = graphSnapshotFactory;
        this.graphShadowFactory = graphShadowFactory;
        this.liveGraphFactory = liveGraphFactory;
        this.diffFactory = diffFactory;
    }

    public Commit create(String author, Object currentVersion){
        Validate.argumentsAreNotNull(author, currentVersion);

        ObjectNode currentGraph = liveGraphFactory.createLiveGraph(currentVersion);
        Optional<ObjectNode> shadowGraph = graphShadowFactory.createLatestShadow(currentVersion);

        //capture current state
        List<CdoSnapshot> snapshots = graphSnapshotFactory.create(currentGraph);

        //do diff
        Diff diff;
        if (shadowGraph.isEmpty()) {
            diff = diffFactory.createInitial(author, currentGraph);
        }   else{
            diff = diffFactory.create(author, shadowGraph.get(), currentGraph);
        }

        return new Commit(author, snapshots, diff);
    }

}
