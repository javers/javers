package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.Cdo;
import org.javers.core.graph.LiveGraph;
import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Builds snapshots for provided live objects.
 * Result contains only snapshots of objects that were newly created or
 * changed (comparing to provided collection of latest snapshots).
 * Result can be easily serialized and persisted.
 */
public class ChangedCdoSnapshotsFactory {

    private final SnapshotFactory snapshotFactory;

    ChangedCdoSnapshotsFactory(SnapshotFactory snapshotFactory) {
        this.snapshotFactory = snapshotFactory;
    }

    /**
     * @param liveGraph wrapped CDOs for which snapshots should be created if they differ from latest snapshots
     * @param latestSnapshots CDO snapshots used to check which of liveObjects have been created or changed
     * @param commitMetadata commit metadata used to create new snapshots
     */
    public List<CdoSnapshot> create(LiveGraph liveGraph, Set<CdoSnapshot> latestSnapshots, CommitMetadata commitMetadata) {
        Validate.argumentsAreNotNull(liveGraph, commitMetadata, latestSnapshots);

        List<CdoSnapshot> result = new ArrayList<>();
        for (Cdo currentCdo : liveGraph.cdos()) {
            Optional<CdoSnapshot> previousSnapshot = latestSnapshots.stream().filter(currentCdo::equals).findFirst();
            CdoSnapshot currentSnapshot = createSnapshot(latestSnapshots, commitMetadata, currentCdo, previousSnapshot);
            if (isCdoChanged(previousSnapshot, currentSnapshot)) {
                result.add(currentSnapshot);
            }
        }
        return result;
    }

    private CdoSnapshot createSnapshot(Set<CdoSnapshot> previousCdos, CommitMetadata commitMetadata,
                                       Cdo liveCdo, Optional<CdoSnapshot> previousSnapshot) {
        return isNewlyCreated(liveCdo, previousCdos) ?
                createInitialSnapshot(commitMetadata, liveCdo) :
                createUpdateSnapshot(commitMetadata, liveCdo, previousSnapshot.get());
    }

    private boolean isNewlyCreated(Cdo cdo, Set<CdoSnapshot> previousCdos) {
        return !previousCdos.contains(cdo);
    }

    private CdoSnapshot createInitialSnapshot(CommitMetadata commitMetadata, Cdo liveCdo) {
        return snapshotFactory.createInitial(liveCdo, commitMetadata);
    }

    private CdoSnapshot createUpdateSnapshot(CommitMetadata commitMetadata, Cdo liveCdo, CdoSnapshot previousSnapshot) {
        return snapshotFactory.createUpdate(liveCdo, previousSnapshot, commitMetadata);
    }

    private boolean isCdoChanged(Optional<CdoSnapshot> previousSnapshot, CdoSnapshot currentSnapshot) {
        return !previousSnapshot.isPresent() || !previousSnapshot.get().stateEquals(currentSnapshot);
    }
}
