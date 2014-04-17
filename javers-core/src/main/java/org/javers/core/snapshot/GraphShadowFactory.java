package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.JaversRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Restores objects graph snapshot
 *
 * @author bartosz walacik
 */
public class GraphShadowFactory {
    private final JaversRepository javersRepository;

    public GraphShadowFactory(JaversRepository javersRepository) {
        this.javersRepository = javersRepository;
    }

    public ShadowGraph createLatestShadow(LiveGraph liveGraph){
        Validate.argumentIsNotNull(liveGraph);


        Set<ObjectNode> snapshotNodes = new HashSet<>();
        for (ObjectNode liveNode : liveGraph.flatten()){
            Optional<CdoSnapshot> snapshot =  javersRepository.getLatest(liveNode.getGlobalCdoId());

            if (snapshot.isEmpty()){
                continue;
            }

            snapshotNodes.add(new ObjectNode(snapshot.get()));
        }

        return new ShadowGraph(snapshotNodes);
    }
}
