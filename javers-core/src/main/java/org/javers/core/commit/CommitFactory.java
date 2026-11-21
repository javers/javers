package org.javers.core.commit;

import org.javers.common.collections.Lists;
import org.javers.common.date.DateProvider;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffBuilder;
import org.javers.core.diff.DiffFactory;
import org.javers.core.graph.Cdo;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.graph.ObjectGraph;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.snapshot.ChangedCdoSnapshotsFactory;
import org.javers.core.snapshot.SnapshotFactory;
import org.javers.core.snapshot.SnapshotGraphFactory;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private static final int ALL = Integer.MAX_VALUE;

    private final DiffFactory diffFactory;
    private final JaversExtendedRepository javersRepository;
    private final DateProvider dateProvider;
    private final LiveGraphFactory liveGraphFactory;
    private final SnapshotFactory snapshotFactory;
    private final SnapshotGraphFactory snapshotGraphFactory;
    private final ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory;
    private final CommitIdFactory commitIdFactory;

    public CommitFactory(DiffFactory diffFactory, JaversExtendedRepository javersRepository, DateProvider dateProvider, LiveGraphFactory liveGraphFactory, SnapshotFactory snapshotFactory, SnapshotGraphFactory snapshotGraphFactory, ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory, CommitIdFactory commitIdFactory) {
        this.diffFactory = diffFactory;
        this.javersRepository = javersRepository;
        this.dateProvider = dateProvider;
        this.liveGraphFactory = liveGraphFactory;
        this.snapshotFactory = snapshotFactory;
        this.snapshotGraphFactory = snapshotGraphFactory;
        this.changedCdoSnapshotsFactory = changedCdoSnapshotsFactory;
        this.commitIdFactory = commitIdFactory;
    }

    public Commit createTerminalByGlobalId(String author, Map<String, String> properties, GlobalId removedId){
        argumentsAreNotNull(author, properties, removedId);
        Optional<CdoSnapshot> previousSnapshot = javersRepository.getLatest(removedId);

        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
        if (previousSnapshot.isPresent() && previousSnapshot.get().isTerminal()) {
            return new Commit(commitMetadata, Collections.emptyList(), DiffBuilder.empty());
        }
        CdoSnapshot terminalSnapshot = snapshotFactory.createTerminal(removedId, previousSnapshot.orElse(null), commitMetadata);
        Diff diff = diffFactory.singleTerminal(removedId, commitMetadata);
        return new Commit(commitMetadata, Lists.asList(terminalSnapshot), diff);
    }

    public Commit createTerminal(String author, Map<String, String> properties, Object removed){
        argumentsAreNotNull(author, properties, removed);
        Cdo removedCdo = liveGraphFactory.createCdo(removed);
        return createTerminalByGlobalId(author, properties, removedCdo.getGlobalId());
    }

    /**
     * @see org.javers.core.Javers#commitAggregateDelete(String, Object)
     */
    public Commit createAggregateTerminal(String author, Map<String, String> properties, Object removed){
        argumentsAreNotNull(author, properties, removed);
        Cdo removedCdo = liveGraphFactory.createCdo(removed);
        return createAggregateTerminalByGlobalId(author, properties, removedCdo.getGlobalId());
    }

    /**
     * @see org.javers.core.Javers#commitAggregateDelete(String, Object)
     */
    public Commit createAggregateTerminalByGlobalId(String author, Map<String, String> properties, GlobalId removedId){
        argumentsAreNotNull(author, properties, removedId);
        if (!(removedId instanceof InstanceId)) {
            return createTerminalByGlobalId(author, properties, removedId);
        }

        Map<GlobalId, CdoSnapshot> latestSnapshots = loadLatestSnapshotsWithChildValueObjects(removedId);
        CdoSnapshot latestRootSnapshot = latestSnapshots.get(removedId);
        if (latestRootSnapshot == null) {
            return createTerminalByGlobalId(author, properties, removedId);
        }

        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
        List<CdoSnapshot> terminalSnapshots = new ArrayList<>();
        Diff diff = DiffBuilder.empty();
        if (!latestRootSnapshot.isTerminal()) {
            terminalSnapshots.add(snapshotFactory.createTerminal(removedId, latestRootSnapshot, commitMetadata));
            diff = diffFactory.singleTerminal(removedId, commitMetadata);
        }
        for (CdoSnapshot latestSnapshot : latestSnapshots.values()) {
            if (isChildValueObject(latestSnapshot.getGlobalId(), removedId) && !latestSnapshot.isTerminal()) {
                terminalSnapshots.add(snapshotFactory.createTerminal(latestSnapshot.getGlobalId(), latestSnapshot, commitMetadata));
            }
        }
        return new Commit(commitMetadata, terminalSnapshots, diff);
    }

    /**
     * Reads the whole history of a given Entity and its child Value Objects in one query,
     * returns the latest snapshot (the highest version) of each of them.
     */
    private Map<GlobalId, CdoSnapshot> loadLatestSnapshotsWithChildValueObjects(GlobalId entityId) {
        QueryParams queryParams = QueryParamsBuilder.withLimit(ALL).withChildValueObjects(true).build();

        Map<GlobalId, CdoSnapshot> latestSnapshots = new LinkedHashMap<>();
        for (CdoSnapshot snapshot : javersRepository.getStateHistory(entityId, queryParams)) {
            latestSnapshots.merge(snapshot.getGlobalId(), snapshot,
                    (one, other) -> one.getVersion() >= other.getVersion() ? one : other);
        }
        return latestSnapshots;
    }

    private boolean isChildValueObject(GlobalId globalId, GlobalId ownerId) {
        return globalId instanceof ValueObjectId && ((ValueObjectId) globalId).getOwnerId().equals(ownerId);
    }

    public Commit create(String author, Map<String, String> properties, Object currentVersion){
        argumentsAreNotNull(author, currentVersion);
        LiveGraph currentGraph = createLiveGraph(currentVersion);
        return createCommit(author, properties, currentGraph);
    }

    private Commit createCommit(String author, Map<String, String> properties, LiveGraph currentGraph){
        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
        ObjectGraph<CdoSnapshot> latestSnapshotGraph = snapshotGraphFactory.loadLatestAndCreateGraph(currentGraph.globalIds());
        List<CdoSnapshot> changedCdoSnapshots =
            changedCdoSnapshotsFactory.create(currentGraph, latestSnapshotGraph.cdos(), commitMetadata);
        Diff diff = diffFactory.create(latestSnapshotGraph, currentGraph, Optional.of(commitMetadata));
        return new Commit(commitMetadata, changedCdoSnapshots, diff);
    }

    private LiveGraph createLiveGraph(Object currentVersion){
        argumentsAreNotNull(currentVersion);
        return liveGraphFactory.createLiveGraph(currentVersion);
    }

    private CommitMetadata newCommitMetadata(String author, Map<String, String> properties){
        ZonedDateTime now = dateProvider.now();
        return new CommitMetadata(author, properties,
                now.toLocalDateTime(), now.toInstant(),
                commitIdFactory.nextId());
    }
}
