package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.*;

import java.util.*;

/**
 * Builds snapshots for provided live objects.
 * Result contains only snapshots of objects that were newly created or
 * has changed as against to provided collection of latest snapshots.
 * Result can be easily serialized and persisted.
 *
 * @author bartosz walacik
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
    public List<CdoSnapshot> create(Set<CdoWrapper> liveObjects, Set<CdoSnapshot> latestSnapshots, CommitMetadata commitMetadata) {
        Validate.argumentsAreNotNull(liveObjects, commitMetadata, latestSnapshots);

        List<CdoSnapshot> result = new ArrayList<>();
        for (CdoWrapper currentCdo : liveObjects) {
            Optional<CdoSnapshot> previousSnapshot = latestSnapshots.stream().filter(currentCdo::equals).findFirst();
            CdoSnapshot currentSnapshot = createFreshSnapshot(latestSnapshots, commitMetadata, currentCdo, previousSnapshot);
            if (isCdoChanged(previousSnapshot, currentSnapshot)) {
                result.add(currentSnapshot);
            }
        }
        return result;
    }

    private CdoSnapshot createFreshSnapshot(Set<CdoSnapshot> previousCdos, CommitMetadata commitMetadata,
                                            CdoWrapper cdoWrapper, Optional<CdoSnapshot> previousSnapshot) {
        return isNewlyCreated(cdoWrapper, previousCdos) ?
                createFreshInitialSnapshot(commitMetadata, cdoWrapper) :
                createFreshUpdateSnapshot(commitMetadata, cdoWrapper, previousSnapshot.get());
    }

    private boolean isNewlyCreated(Cdo cdo, Set<CdoSnapshot> previousCdos) {
        return !previousCdos.contains(cdo);
    }

    private CdoSnapshot createFreshInitialSnapshot(CommitMetadata commitMetadata, CdoWrapper cdoWrapper) {
        return snapshotFactory.createInitial(cdoWrapper, commitMetadata);
    }

    private CdoSnapshot createFreshUpdateSnapshot(CommitMetadata commitMetadata, CdoWrapper cdoWrapper, CdoSnapshot previousSnapshot) {
        return snapshotFactory.createUpdate(cdoWrapper, previousSnapshot, commitMetadata);
    }

    private boolean isCdoChanged(Optional<CdoSnapshot> previousSnapshot, CdoSnapshot currentSnapshot) {
        return !previousSnapshot.isPresent() || !previousSnapshot.get().stateEquals(currentSnapshot);
    }
}
