package org.javers.core.commit;

import org.javers.common.validation.Validate;
import org.javers.core.GraphFactory;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.graph.LiveGraph;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.snapshot.ShadowGraph;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private final DiffFactory diffFactory;
    private final JaversExtendedRepository javersRepository;
    private final GraphFactory graphFactory;
    private final CommitSeqGenerator commitSeqGenerator;

    public CommitFactory(DiffFactory diffFactory, JaversExtendedRepository javersRepository, GraphFactory graphFactory, CommitSeqGenerator commitSeqGenerator) {
        this.diffFactory = diffFactory;
        this.javersRepository = javersRepository;
        this.graphFactory = graphFactory;
        this.commitSeqGenerator = commitSeqGenerator;
    }

    public Commit create(String author, Object currentVersion){
        Validate.argumentsAreNotNull(author, currentVersion);

        CommitId head = javersRepository.getHeadId();
        CommitId newId = commitSeqGenerator.nextId(head);

        LiveGraph currentGraph = graphFactory.createLiveGraph(currentVersion);
        ShadowGraph latestShadowGraph = graphFactory.createLatestShadow(currentGraph);

        //capture current state
        List<CdoSnapshot> snapshots = graphFactory.createGraphSnapshot(currentGraph);

        //do diff
        Diff diff = diffFactory.create(latestShadowGraph, currentGraph);

        return new Commit(newId, author, snapshots, diff);
    }

}
