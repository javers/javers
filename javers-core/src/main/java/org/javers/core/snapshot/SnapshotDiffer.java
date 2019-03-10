package org.javers.core.snapshot;

import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotBuilder;
import org.javers.repository.api.SnapshotIdentifier;

import java.util.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class SnapshotDiffer {

    private final DiffFactory diffFactory;

    public SnapshotDiffer(DiffFactory diffFactory) {
        this.diffFactory = diffFactory;
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
                addInitialChanges(changes, snapshot);
            } else if (snapshot.isTerminal()) {
                addTerminalChanges(changes, snapshot);
            } else {
                CdoSnapshot previousSnapshot = previousSnapshots.get(SnapshotIdentifier.from(snapshot).previous());
                addChanges(changes, previousSnapshot, snapshot);
            }
        }
        return changes;
    }

    private void addInitialChanges(List<Change> changes, CdoSnapshot initialSnapshot) {
        CdoSnapshot emptySnapshot = CdoSnapshotBuilder.emptyCopyOf(initialSnapshot).build();
        Diff diff = diffFactory.create(snapshotGraph(emptySnapshot), snapshotGraph(initialSnapshot),
            commitMetadata(initialSnapshot));
        NewObject newObjectChange =
            new NewObject(initialSnapshot.getGlobalId(), empty(), of(initialSnapshot.getCommitMetadata()));
        changes.addAll(diff.getChanges());
        changes.add(newObjectChange);
    }

    private void addTerminalChanges(List<Change> changes, CdoSnapshot terminalSnapshot) {
        changes.add(new ObjectRemoved(terminalSnapshot.getGlobalId(), empty(), of(terminalSnapshot.getCommitMetadata())));
    }

    private void addChanges(List<Change> changes, CdoSnapshot previousSnapshot, CdoSnapshot currentSnapshot) {
        Diff diff = diffFactory.create(snapshotGraph(previousSnapshot), snapshotGraph(currentSnapshot),
            commitMetadata(currentSnapshot));
        changes.addAll(diff.getChanges());
    }

    private SnapshotGraph snapshotGraph(CdoSnapshot snapshot) {
        return new SnapshotGraph(Sets.asSet(new SnapshotNode(snapshot)));
    }

    private Optional<CommitMetadata> commitMetadata(CdoSnapshot snapshot) {
        return of(snapshot.getCommitMetadata());
    }
}
