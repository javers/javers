package org.javers.core.commit;

import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.snapshot.GraphSnapshotFactory;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private final GraphSnapshotFactory graphSnapshotFactory;

    public CommitFactory(GraphSnapshotFactory graphSnapshotFactory) {
        this.graphSnapshotFactory = graphSnapshotFactory;
    }

    public Commit create(String author, ObjectNode currentVersion){
        Validate.argumentsAreNotNull(author, currentVersion);

        List<CdoSnapshot> snapshots = graphSnapshotFactory.create(currentVersion);

        return new Commit(author, snapshots);
    }
}
