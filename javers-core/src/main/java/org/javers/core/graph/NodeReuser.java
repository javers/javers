package org.javers.core.graph;

import org.javers.core.metamodel.object.Cdo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
class NodeReuser {
    private final Map<Cdo, ObjectNode> reverseCdoIdMap;
    private int reusedNodes;

    NodeReuser() {
        this.reverseCdoIdMap = new HashMap<>();
    }

    boolean isReusable(Cdo cdo){
        return reverseCdoIdMap.containsKey(cdo);
    }

    ObjectNode getForReuse(Cdo cdo) {
        reusedNodes++;
        return reverseCdoIdMap.get(cdo);
    }

    void saveForReuse(ObjectNode reference) {
        reverseCdoIdMap.put(reference.getCdo(), reference);
    }

    public int nodesCount() {
        return reverseCdoIdMap.size();
    }

    public int reusedNodesCount() {
        return reusedNodes;
    }
}
