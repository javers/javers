package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectNode;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.HashSet;
import java.util.Set;


/**
 * Builds ShadowGraph from latest snapshots loaded from javersRepository
 *
 * @author bartosz walacik
 */
class GraphShadowFactory {
    private final JaversExtendedRepository javersRepository;

    GraphShadowFactory(JaversExtendedRepository javersRepository) {
        this.javersRepository = javersRepository;
    }

    ShadowGraph createLatestShadow(LiveGraph liveGraph){
        Validate.argumentIsNotNull(liveGraph);

        Set<ObjectNode> snapshotNodes = new HashSet<>();
        for (ObjectNode liveNode : liveGraph.nodes()){
            javersRepository.getLatest(liveNode.getGlobalId())
                    .map(s -> snapshotNodes.add(new ObjectNode(s)));
        }

        return new ShadowGraph(snapshotNodes);
    }
}
