package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.*;

import java.util.*;

/**
 * Decomposes given live object graph into a flat list of object Snapshots.
 * List contains only snapshots of objects that were newly created or changed as against to provided snapshot graph.
 * Resulting structure can be easily serialized and persisted.
 *
 * @author bartosz walacik
 */
public class ChangedCdoSnapshotsFactory {

    private final SnapshotFactory snapshotFactory;

    ChangedCdoSnapshotsFactory(SnapshotFactory snapshotFactory) {
        this.snapshotFactory = snapshotFactory;
    }

    /**
     * @param currentCdos wrapped CDOs for which snapshot should be created if it differs from previous snapshot
     * @param previousCdos CDO snapshots used to check which of currentCdos have changed
     * @param commitMetadata commit metadata used to create non-initial snapshots
     */
    public List<CdoSnapshot> create(Set<CdoWrapper> currentCdos, Set<CdoSnapshot> previousCdos, CommitMetadata commitMetadata) {
        Validate.argumentsAreNotNull(currentCdos, commitMetadata, previousCdos);

        List<CdoSnapshot> result = new ArrayList<>();
        for (CdoWrapper currentCdo : currentCdos) {
            Optional<CdoSnapshot> previousSnapshot = previousCdos.stream().filter(currentCdo::equals).findFirst();
            CdoSnapshot currentSnapshot = createFreshSnapshot(previousCdos, commitMetadata, currentCdo, previousSnapshot);
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
