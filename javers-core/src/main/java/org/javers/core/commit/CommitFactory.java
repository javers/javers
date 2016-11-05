package org.javers.core.commit;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.date.DateProvider;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.snapshot.GraphSnapshotFacade;
import org.javers.core.snapshot.ShadowGraph;
import org.javers.core.snapshot.SnapshotFactory;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.List;
import java.util.Map;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private final DiffFactory diffFactory;
    private final JaversExtendedRepository javersRepository;
    private final DateProvider dateProvider;
    private final GraphSnapshotFacade graphSnapshotFacade;
    private final LiveGraphFactory liveGraphFactory;
    private final SnapshotFactory snapshotFactory;
    private final CommitIdFactory commitIdFactory;

    public CommitFactory(DiffFactory diffFactory, JaversExtendedRepository javersRepository, DateProvider dateProvider, GraphSnapshotFacade graphSnapshotFacade, LiveGraphFactory liveGraphFactory, SnapshotFactory snapshotFactory, CommitIdFactory commitIdFactory) {
        this.diffFactory = diffFactory;
        this.javersRepository = javersRepository;
        this.dateProvider = dateProvider;
        this.graphSnapshotFacade = graphSnapshotFacade;
        this.liveGraphFactory = liveGraphFactory;
        this.snapshotFactory = snapshotFactory;
        this.commitIdFactory = commitIdFactory;
    }

    public Commit createTerminalByGlobalId(String author, Map<String, String> properties, GlobalId removedId){
        Validate.argumentsAreNotNull(author, properties, removedId);
        Optional<CdoSnapshot> previousSnapshot = javersRepository.getLatest(removedId);
        if (previousSnapshot.isEmpty()){
            throw new JaversException(JaversExceptionCode.CANT_DELETE_OBJECT_NOT_FOUND,removedId.value());
        }
        CommitMetadata commitMetadata = nextCommit(author, properties);
        CdoSnapshot terminalSnapshot = snapshotFactory.createTerminal(removedId, previousSnapshot.get(), commitMetadata);
        Diff diff = diffFactory.singleTerminal(removedId, commitMetadata);
        return new Commit(commitMetadata, Lists.asList(terminalSnapshot), diff);
    }

    public Commit createTerminal(String author, Map<String, String> properties, Object removed){
        Validate.argumentsAreNotNull(author, properties, removed);
        Cdo removedCdo = liveGraphFactory.createCdo(removed);
        return createTerminalByGlobalId(author, properties, removedCdo.getGlobalId());
    }

    public Commit create(String author, Map<String, String> properties, Object currentVersion){
        Validate.argumentsAreNotNull(author, currentVersion);
        CommitMetadata commitMetadata = nextCommit(author, properties);
        LiveGraph currentGraph = liveGraphFactory.createLiveGraph(currentVersion);
        ShadowGraph latestShadowGraph = graphSnapshotFacade.createLatestShadow(currentGraph);
        List<CdoSnapshot> snapshots = graphSnapshotFacade.createGraphSnapshot(currentGraph, latestShadowGraph, commitMetadata);
        Diff diff = diffFactory.create(latestShadowGraph, currentGraph, Optional.of(commitMetadata));
        return new Commit(commitMetadata, snapshots, diff);
    }

    private CommitMetadata nextCommit(String author, Map<String, String> properties){
        return new CommitMetadata(author, properties, dateProvider.now(), commitIdFactory.nextId());
    }
}
