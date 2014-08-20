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
    private final Map<Object, ObjectNode> reverseCdoIdMap;
    private int reusedNodes;
    private int entities;
    private int valueObjects;

    NodeReuser() {
        this.reverseCdoIdMap = new HashMap<>();
    }

    boolean isReusable(Cdo cdo){
        return reverseCdoIdMap.containsKey(reverseCdoIdMapKey(cdo));
    }

    ObjectNode getForReuse(Cdo cdo) {
        reusedNodes++;
        return reverseCdoIdMap.get(reverseCdoIdMapKey(cdo));
    }

    void saveForReuse(ObjectNode reference) {
        if (reference.getGlobalId() instanceof InstanceId){
            entities++;
        }
        if (reference.getGlobalId() instanceof ValueObjectId){
            valueObjects++;
        }
        reverseCdoIdMap.put(reverseCdoIdMapKey(reference.getCdo()), reference);
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

    /**
     * InstanceId for Entities,
     * System.identityHashCode for ValueObjects
     */
    private Object reverseCdoIdMapKey(Cdo cdo) {
          if (cdo.getGlobalId() instanceof InstanceId){
              return cdo.getGlobalId();
          }
          return System.identityHashCode(cdo.getWrappedCdo().get());
    }
}
