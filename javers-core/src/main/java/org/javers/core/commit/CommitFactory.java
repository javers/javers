package org.javers.core.commit;

import org.javers.common.collections.Lists;
import org.javers.common.date.DateProvider;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.graph.Cdo;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.graph.ObjectGraph;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.snapshot.ChangedCdoSnapshotsFactory;
import org.javers.core.snapshot.SnapshotFactory;
import org.javers.core.snapshot.SnapshotGraphFactory;
import org.javers.repository.api.JaversExtendedRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        CdoSnapshot terminalSnapshot = previousSnapshot
                .map(prev -> snapshotFactory.createTerminal(removedId, prev, commitMetadata))
                .orElseThrow(() -> new JaversException(JaversExceptionCode.CANT_DELETE_OBJECT_NOT_FOUND, removedId.value()));
        Diff diff = diffFactory.singleTerminal(removedId, commitMetadata);
        return new Commit(commitMetadata, Lists.asList(terminalSnapshot), diff);
    }

    public Commit createTerminal(String author, Map<String, String> properties, Object removed){
        argumentsAreNotNull(author, properties, removed);
        Cdo removedCdo = liveGraphFactory.createCdo(removed);
        return createTerminalByGlobalId(author, properties, removedCdo.getGlobalId());
    }

    public Commit create(String author, Map<String, String> properties, Object currentVersion){
        argumentsAreNotNull(author, currentVersion);
        LiveGraph currentGraph = createLiveGraph(currentVersion);
        return createCommit(author, properties, currentGraph);
    }

    private Commit createCommit(String author, Map<String, String> properties, LiveGraph currentGraph){
        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
        ObjectGraph<CdoSnapshot> latestSnapshotGraph = snapshotGraphFactory.createLatest(currentGraph.globalIds());
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
