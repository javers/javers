package org.javers.core.commit;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.GraphFactory;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.api.JaversRepository;

import java.util.List;

import static org.javers.core.diff.DiffBuilder.empty;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private final DiffFactory diffFactory;
    private final JaversRepository javersRepository;
    private final GraphFactory graphFactory;
    private final CommitSeqGenerator commitSeqGenerator;

    public CommitFactory(DiffFactory diffFactory, JaversRepository javersRepository, GraphFactory graphFactory, CommitSeqGenerator commitSeqGenerator) {
        this.diffFactory = diffFactory;
        this.javersRepository = javersRepository;
        this.graphFactory = graphFactory;
        this.commitSeqGenerator = commitSeqGenerator;
    }

    public Commit create(String author, Object currentVersion){
        Validate.argumentsAreNotNull(author, currentVersion);

        CommitId head = javersRepository.getHeadId();
        CommitId newId = commitSeqGenerator.nextId(head);

        ObjectNode currentGraph = graphFactory.createLiveGraph(currentVersion);
        Optional<ObjectNode> shadowGraph = graphFactory.createLatestShadow(currentVersion);

        //capture current state
        List<CdoSnapshot> snapshots = graphFactory.createGraphSnapshot(currentGraph);

        //do diff
        Diff diff;
        if (shadowGraph.isEmpty()) {
            diff = diffFactory.createInitial(currentGraph);
        }   else{
            diff = diffFactory.create(shadowGraph.get(), currentGraph);
        }

        return new Commit(newId, author, snapshots, diff);
    }

}
