package org.javers.core.commit;

import org.javers.common.collections.Lists;
import org.javers.common.date.DateProvider;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.graph.Cdo;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.graph.ObjectGraph;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.scanner.CascadeFallbackEvaluator;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.snapshot.ChangedCdoSnapshotsFactory;
import org.javers.core.snapshot.SnapshotFactory;
import org.javers.core.snapshot.SnapshotGraphFactory;
import org.javers.repository.api.JaversExtendedRepository;
import org.jspecify.annotations.NonNull;

import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private final DiffFactory diffFactory;
    private final JaversExtendedRepository javersRepository;
    private final DateProvider dateProvider;
    private final LiveGraphFactory liveGraphFactory;
    private final SnapshotFactory snapshotFactory;
    private final SnapshotGraphFactory snapshotGraphFactory;
    private final ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory;
    private final CommitIdFactory commitIdFactory;
    private final CascadeFallbackEvaluator cascadeFallbackEvaluator;

    public CommitFactory(DiffFactory diffFactory, JaversExtendedRepository javersRepository, DateProvider dateProvider, LiveGraphFactory liveGraphFactory, SnapshotFactory snapshotFactory, SnapshotGraphFactory snapshotGraphFactory, ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory, CommitIdFactory commitIdFactory, final CascadeFallbackEvaluator cascadeFallbackEvaluator) {
        this.diffFactory = diffFactory;
        this.javersRepository = javersRepository;
        this.dateProvider = dateProvider;
        this.liveGraphFactory = liveGraphFactory;
        this.snapshotFactory = snapshotFactory;
        this.snapshotGraphFactory = snapshotGraphFactory;
        this.changedCdoSnapshotsFactory = changedCdoSnapshotsFactory;
        this.commitIdFactory = commitIdFactory;
        this.cascadeFallbackEvaluator = cascadeFallbackEvaluator;
    }

    public Commit createTerminalByGlobalId(String author, Map<String, String> properties, GlobalId removedId){
        argumentsAreNotNull(author, properties, removedId);
        Optional<CdoSnapshot> previousSnapshot = javersRepository.getLatest(removedId);

        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
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
     * @see org.javers.core.Javers#commitCascadingDelete(String, Object)
     */
    public Commit createCascadingTerminal(String author, Map<String, String> properties, Object removed){
        argumentsAreNotNull(author, properties, removed);
        Cdo removedCdo = liveGraphFactory.createCdo(removed);
        return createCascadingTerminalByGlobalId(author, properties, removedCdo.getGlobalId());
    }

    /**
     * @see org.javers.core.Javers#commitCascadingDelete(String, Object)
     */
    public Commit createCascadingTerminalByGlobalId(String author, Map<String, String> properties, GlobalId removedId){
        argumentsAreNotNull(author, properties, removedId);

        CommitMetadata commitMetadata = newCommitMetadata(author, properties);

        List<GlobalId> idsToDelete = collectCascadingIds(removedId);

        List<CdoSnapshot> terminalSnapshots = new ArrayList<>();
        for (GlobalId id : idsToDelete) {
            Optional<CdoSnapshot> previousSnapshot = javersRepository.getLatest(id);
            terminalSnapshots.add(snapshotFactory.createTerminal(id, previousSnapshot.orElse(null), commitMetadata));
        }

        Diff diff = diffFactory.multiTerminal(idsToDelete, commitMetadata);
        return new Commit(commitMetadata, terminalSnapshots, diff);
    }

    /**
     * Breadth-first traversal of the object graph starting at {@code rootId},
     * following only references held in properties annotated with {@code @Cascading}.
     */
    private List<GlobalId> collectCascadingIds(GlobalId rootId) {
        List<GlobalId> result = new ArrayList<>();
        Set<GlobalId> visited = new LinkedHashSet<>();
        Deque<GlobalId> queue = new ArrayDeque<>();

        visited.add(rootId);
        queue.add(rootId);

        while (!queue.isEmpty()) {
            GlobalId currentId = queue.poll();
            result.add(currentId);

            Optional<CdoSnapshot> currentSnapshot = javersRepository.getLatest(currentId);
            if (!currentSnapshot.isPresent()) {
                continue;
            }

            CdoSnapshot snapshot = currentSnapshot.get();
            for (JaversProperty property : snapshot.getManagedType().getProperties(this::shouldCascade)) {
                for (GlobalId reference : extractReferences(snapshot, property)) {
                    if (visited.add(reference)) {
                        queue.add(reference);
                    }
                }
            }
        }

        return result;
    }

    private boolean shouldCascade(final JaversProperty property) {
        return property.shouldCascade().orElseGet(() -> cascadeFallbackEvaluator.shouldCascade(property.getType()));
    }

    private List<GlobalId> extractReferences(CdoSnapshot snapshot, JaversProperty property) {
        Object propertyValue = snapshot.getPropertyValue(property);
        if (propertyValue == null) {
            return Collections.emptyList();
        }
        if (propertyValue instanceof GlobalId) {
            return Collections.singletonList((GlobalId) propertyValue);
        }
        if (property.getType() instanceof EnumerableType) {
            EnumerableType enumerableType = property.getType();
            return enumerableType.filterToList(propertyValue, GlobalId.class);
        }
        return Collections.emptyList();
    }

    public Commit create(String author, Map<String, String> properties, Object currentVersion){
        argumentsAreNotNull(author, currentVersion);
        // When deleting, creating and deleting an entity again, the value object is not deleted in the last commit
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
