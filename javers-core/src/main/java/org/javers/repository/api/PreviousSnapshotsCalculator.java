package org.javers.repository.api;

import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.javers.common.collections.Lists.toImmutableList;

class PreviousSnapshotsCalculator {

    private final Function<Collection<SnapshotIdentifier>, List<CdoSnapshot>> snapshotProvider;

    PreviousSnapshotsCalculator(Function<Collection<SnapshotIdentifier>, List<CdoSnapshot>> snapshotProvider) {
        this.snapshotProvider = snapshotProvider;
    }

    /**
     * Returns a Map from snapshot.id to snapshot with this id.
     * The Map contains entries for all previous snapshots.
     * I.e, for each snapshot S from a given list, there is a map entry for S.id.previous()
     */
    Map<SnapshotIdentifier, CdoSnapshot> calculate(List<CdoSnapshot> snapshots) {
        Map<SnapshotIdentifier, CdoSnapshot> previousSnapshots = new HashMap<>();
        populatePreviousSnapshotsWithSnapshots(previousSnapshots, snapshots);
        List<CdoSnapshot> missingPreviousSnapshots = getMissingPreviousSnapshots(snapshots, previousSnapshots);
        populatePreviousSnapshotsWithSnapshots(previousSnapshots, missingPreviousSnapshots);
        return previousSnapshots;
    }

    private List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> identifiers) {
        return snapshotProvider.apply(identifiers);
    }

    private void populatePreviousSnapshotsWithSnapshots(Map<SnapshotIdentifier, CdoSnapshot> previousSnapshots, List<CdoSnapshot> snapshots) {
        for (CdoSnapshot snapshot : snapshots) {
            previousSnapshots.put(SnapshotIdentifier.from(snapshot), snapshot);
        }
    }

    private List<CdoSnapshot> getMissingPreviousSnapshots(List<CdoSnapshot> snapshots, Map<SnapshotIdentifier, CdoSnapshot> previousSnapshots) {
        List<SnapshotIdentifier> missingPreviousSnapshotIdentifiers =
                determineMissingPreviousSnapshotIdentifiers(previousSnapshots, snapshots);
        return getSnapshots(missingPreviousSnapshotIdentifiers);
    }

    private List<SnapshotIdentifier> determineMissingPreviousSnapshotIdentifiers(Map<SnapshotIdentifier, CdoSnapshot> previousSnapshots, List<CdoSnapshot> snapshots) {
        List<SnapshotIdentifier> missingPreviousSnapshotIdentifiers = snapshots.stream()
                .filter(snapshot -> !(snapshot.isInitial() || snapshot.isTerminal()))
                .map(snapshot -> SnapshotIdentifier.from(snapshot).previous())
                .filter(previousSnapshotIdentifier -> !previousSnapshots.containsKey(previousSnapshotIdentifier))
                .collect(toImmutableList());
        return missingPreviousSnapshotIdentifiers;
    }
}
