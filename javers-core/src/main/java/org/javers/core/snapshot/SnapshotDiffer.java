package org.javers.core.snapshot;

import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.core.CoreConfiguration;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotBuilder;
import org.javers.repository.api.SnapshotIdentifier;

import java.util.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;
import static org.javers.core.metamodel.object.SnapshotType.TERMINAL;

public class SnapshotDiffer {

    private final DiffFactory diffFactory;
    private final CoreConfiguration javersCoreConfiguration;

    public SnapshotDiffer(DiffFactory diffFactory, CoreConfiguration javersCoreConfiguration) {
        this.diffFactory = diffFactory;
        this.javersCoreConfiguration = javersCoreConfiguration;
    }

    /**
     * Calculates changes introduced by a collection of snapshots. This method expects that
     * the previousSnapshots map contains predecessors of all non-initial and non-terminal snapshots.
     */
    public List<Change> calculateDiffs(List<CdoSnapshot> snapshots, Map<SnapshotIdentifier, CdoSnapshot> previousSnapshots) {
        Validate.argumentsAreNotNull(snapshots);
        Validate.argumentsAreNotNull(previousSnapshots);

        List<Change> changes = new ArrayList<>();
        for (CdoSnapshot snapshot : snapshots) {
            if (snapshot.isInitial()) {
                changes.addAll(addInitialChanges(snapshot));
            }
            if (snapshot.isTerminal() && !snapshot.isFirstVersion()) {
                CdoSnapshot previousSnapshot = previousSnapshots.get(SnapshotIdentifier.from(snapshot).previous());
                addTerminalChanges(changes, snapshot, previousSnapshot);
            }
            if (snapshot.isUpdate()) {
                CdoSnapshot previousSnapshot = previousSnapshots.get(SnapshotIdentifier.from(snapshot).previous());
                addChanges(changes, previousSnapshot, snapshot);
            }
        }
        return changes;
    }

    private List<Change> addInitialChanges(CdoSnapshot initialSnapshot) {
        Diff initialDiff = diffFactory.create(emptySnapshotGraph(), snapshotGraph(initialSnapshot), commitMetadata(initialSnapshot));
        return initialDiff.getChanges();
    }

    private void addTerminalChanges(List<Change> changes, CdoSnapshot terminalSnapshot, CdoSnapshot previousSnapshot) {
        changes.add(new ObjectRemoved(terminalSnapshot.getGlobalId(), empty(), of(terminalSnapshot.getCommitMetadata())));
        if (previousSnapshot != null && javersCoreConfiguration.isTerminalChanges()) {
            CdoSnapshot terminalSnapshotWithEmptyState = cdoSnapshot()
                    .withGlobalId(terminalSnapshot.getGlobalId())
                    .withManagedType(terminalSnapshot.getManagedType())
                    .withCommitMetadata(terminalSnapshot.getCommitMetadata())
                    .withType(terminalSnapshot.getType())
                    .withVersion(terminalSnapshot.getVersion())
                    .build();
            Diff terminalDiff = diffFactory.create(snapshotGraph(previousSnapshot), snapshotGraph(terminalSnapshotWithEmptyState), commitMetadata(terminalSnapshot));
            changes.addAll(terminalDiff.getChanges());
        }
    }

    private void addChanges(List<Change> changes, CdoSnapshot previousSnapshot, CdoSnapshot currentSnapshot) {
        Diff diff = diffFactory.create(snapshotGraph(previousSnapshot), snapshotGraph(currentSnapshot), commitMetadata(currentSnapshot));
        changes.addAll(diff.getChanges());
    }

    private SnapshotGraph snapshotGraph(CdoSnapshot snapshot) {
        return new SnapshotGraph(Sets.asSet(new SnapshotNode(snapshot)));
    }

    private SnapshotGraph emptySnapshotGraph() {
        return new SnapshotGraph(Collections.emptySet());
    }

    private Optional<CommitMetadata> commitMetadata(CdoSnapshot snapshot) {
        return of(snapshot.getCommitMetadata());
    }
}
