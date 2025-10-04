package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.*;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds snapshots for provided live objects.
 * Result contains only snapshots of objects that were newly created or
 * changed (comparing to provided collection of latest snapshots).
 * Result can be easily serialized and persisted.
 */
public class ChangedCdoSnapshotsFactory {

    private final SnapshotFactory snapshotFactory;
    private final SnapshotGraphFactory snapshotGraphFactory;

    ChangedCdoSnapshotsFactory(SnapshotFactory snapshotFactory, SnapshotGraphFactory snapshotGraphFactory) {
        this.snapshotFactory = snapshotFactory;
        this.snapshotGraphFactory = snapshotGraphFactory;
    }

    /**
     * @param liveGraph wrapped CDOs for which snapshots should be created if they differ from latest snapshots
     * @param latestSnapshots CDO snapshots used to check which of liveObjects have been created or changed
     * @param commitMetadata commit metadata used to create new snapshots
     */
    public List<CdoSnapshot> create(LiveGraph liveGraph, Set<CdoSnapshot> latestSnapshots, CommitMetadata commitMetadata) {
        Validate.argumentsAreNotNull(liveGraph, commitMetadata, latestSnapshots);

        List<CdoSnapshot> result = new ArrayList<>();
        for (ObjectNode node : liveGraph.nodes()) {
            LiveNode liveNode = (LiveNode) node;
            Cdo currentCdo = node.getCdo();

            Optional<CdoSnapshot> previousSnapshot = latestSnapshots.stream().filter(currentCdo::equals).findFirst();
            CdoSnapshot currentSnapshot = createSnapshot(commitMetadata, liveNode, previousSnapshot);
            if (isCdoChanged(previousSnapshot, currentSnapshot)) {
                result.add(currentSnapshot);

                result.addAll(createTerminalVoSnapshots(previousSnapshot, currentSnapshot, commitMetadata));

            }
        }
        return result;
    }

    private List<CdoSnapshot> createTerminalVoSnapshots(Optional<CdoSnapshot> previousSnapshot, CdoSnapshot currentSnapshot, CommitMetadata commitMetadata){
        if (!previousSnapshot.isPresent()){
           return Collections.emptyList();
        }

        List<GlobalId> removedVO = new ArrayList<>();

        for (JaversProperty property : currentSnapshot.getManagedType().getProperties()) {
            if (!property.isValueObjectType()) {
                continue;
            }

            Object oldVal = previousSnapshot.get().getPropertyValue(property);
            Object newVal = currentSnapshot.getPropertyValue(property);

            if (oldVal != null && newVal == null) {
                removedVO.add((GlobalId) oldVal);
            }
        }

        return snapshotGraphFactory.loadLatest(removedVO).stream()
                .map(s -> snapshotFactory.createTerminal(s.getGlobalId(), s, commitMetadata))
                .collect(Collectors.toList());
    }

    private CdoSnapshot createSnapshot(CommitMetadata commitMetadata,
                                       LiveNode liveNode, Optional<CdoSnapshot> previousSnapshot) {
        return isNewlyCreated(previousSnapshot) ?
                createInitialSnapshot(commitMetadata, liveNode) :
                createUpdateSnapshot(commitMetadata, liveNode, previousSnapshot.get());
    }

    private boolean isNewlyCreated(Optional<CdoSnapshot> previousSnapshot) {
        return !previousSnapshot.isPresent();
    }

    private CdoSnapshot createInitialSnapshot(CommitMetadata commitMetadata, LiveNode liveNode) {
        return snapshotFactory.createInitial(liveNode, commitMetadata);
    }

    private CdoSnapshot createUpdateSnapshot(CommitMetadata commitMetadata, LiveNode liveNode, CdoSnapshot previousSnapshot) {
        return snapshotFactory.createUpdate(liveNode, previousSnapshot, commitMetadata);
    }

    private boolean isCdoChanged(Optional<CdoSnapshot> previousSnapshot, CdoSnapshot currentSnapshot) {
        return !previousSnapshot.isPresent() || !previousSnapshot.get().stateEquals(currentSnapshot);
    }
}
