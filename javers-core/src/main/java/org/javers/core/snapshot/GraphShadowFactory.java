package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.HashSet;
import java.util.Set;


/**
 * Builds ShadowGraph from latest snapshots loaded from javersRepository
 *
 * @author bartosz walacik
 */
public class GraphShadowFactory {
    private final JaversExtendedRepository javersRepository;

    public GraphShadowFactory(JaversExtendedRepository javersRepository) {
        this.javersRepository = javersRepository;
    }

    public ShadowGraph createLatestShadow(LiveGraph liveGraph){
        Validate.argumentIsNotNull(liveGraph);


        Set<ObjectNode> snapshotNodes = new HashSet<>();
        for (ObjectNode liveNode : liveGraph.flatten()){
            Optional<CdoSnapshot> snapshot =  javersRepository.getLatest(liveNode.getGlobalId());

            if (snapshot.isEmpty()){
                continue;
            }

            snapshotNodes.add(new ObjectNode(snapshot.get()));
        }

        return new ShadowGraph(snapshotNodes);
    }

    public ShadowGraph createFromSnapshot(CdoSnapshot cdoSnapshot){
        return new ShadowGraph(Sets.asSet(new ObjectNode(cdoSnapshot)));
    }
}
