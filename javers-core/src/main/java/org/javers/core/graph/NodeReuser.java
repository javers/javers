package org.javers.core.graph;

import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
class NodeReuser {
    private final Map<Cdo, ObjectNode> reverseCdoIdMap;
    private int reusedNodes;
    private int entities;
    private int valueObjects;

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
        if (reference.getGlobalCdoId() instanceof InstanceId){
            entities++;
        }
        if (reference.getGlobalCdoId() instanceof ValueObjectId){
            valueObjects++;
        }
        reverseCdoIdMap.put(reference.getCdo(), reference);
    }

    public int nodesCount() {
        return reverseCdoIdMap.size();
    }

    public int reusedNodesCount() {
        return reusedNodes;
    }

    public int entitiesCount() {
        return entities;
    }

    public int voCount() {
        return valueObjects;
    }
}
