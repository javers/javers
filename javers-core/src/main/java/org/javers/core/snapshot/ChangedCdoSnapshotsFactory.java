package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.*;

import java.util.*;

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
     * @param liveObjects wrapped CDOs for which snapshot should be created if it differs from latest snapshot
     * @param latestSnapshots CDO snapshots used to check which of liveObjects have been created or changed
     * @param commitMetadata commit metadata used to create new snapshots
     */
    public List<CdoSnapshot> create(Set<LiveCdo> liveObjects, Set<CdoSnapshot> latestSnapshots, CommitMetadata commitMetadata) {
        Validate.argumentsAreNotNull(liveObjects, commitMetadata, latestSnapshots);

        List<CdoSnapshot> result = new ArrayList<>();
        for (LiveCdo currentCdo : liveObjects) {
            Optional<CdoSnapshot> previousSnapshot = latestSnapshots.stream().filter(currentCdo::equals).findFirst();
            CdoSnapshot currentSnapshot = createSnapshot(latestSnapshots, commitMetadata, currentCdo, previousSnapshot);
            if (isCdoChanged(previousSnapshot, currentSnapshot)) {
                result.add(currentSnapshot);
            }
        }
        return result;
    }

    private CdoSnapshot createSnapshot(Set<CdoSnapshot> previousCdos, CommitMetadata commitMetadata,
                                       LiveCdo liveCdo, Optional<CdoSnapshot> previousSnapshot) {
        return isNewlyCreated(liveCdo, previousCdos) ?
                createInitialSnapshot(commitMetadata, liveCdo) :
                createUpdateSnapshot(commitMetadata, liveCdo, previousSnapshot.get());
    }

    private boolean isNewlyCreated(Cdo cdo, Set<CdoSnapshot> previousCdos) {
        return !previousCdos.contains(cdo);
    }

    private CdoSnapshot createInitialSnapshot(CommitMetadata commitMetadata, LiveCdo liveCdo) {
        return snapshotFactory.createInitial(liveCdo, commitMetadata);
    }

    private CdoSnapshot createUpdateSnapshot(CommitMetadata commitMetadata, LiveCdo liveCdo, CdoSnapshot previousSnapshot) {
        return snapshotFactory.createUpdate(liveCdo, previousSnapshot, commitMetadata);
    }

    private boolean isCdoChanged(Optional<CdoSnapshot> previousSnapshot, CdoSnapshot currentSnapshot) {
        return !previousSnapshot.isPresent() || !previousSnapshot.get().stateEquals(currentSnapshot);
    }
}
