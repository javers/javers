package org.javers.core.graph;

import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;

import java.util.*;

/**
 * @author bartosz walacik
 */
class NodeReuser {
    private final Map<Object, ObjectNode> reverseCdoIdMap = new HashMap<>();
    private final Queue<ObjectNode> stubs = new LinkedList<>();
    private int reusedNodes;
    private int entities;
    private int valueObjects;

    NodeReuser() {
    }

    boolean isReusable(Cdo cdo) {
        return reverseCdoIdMap.containsKey(reverseCdoIdMapKey(cdo));
    }

    ObjectNode getForReuse(Cdo cdo) {
        reusedNodes++;
        return reverseCdoIdMap.get(reverseCdoIdMapKey(cdo));
    }

    void saveForReuse(ObjectNode reference) {
        if (reference.getGlobalId() instanceof InstanceId) {
            entities++;
        }
        if (reference.getGlobalId() instanceof ValueObjectId) {
            valueObjects++;
        }
        reverseCdoIdMap.put(reverseCdoIdMapKey(reference.getCdo()), reference);
    }

    void enqueueStub(ObjectNode nodeStub) {
        stubs.offer(nodeStub);
    }

    ObjectNode pollStub(){
       return stubs.poll();
    }

    boolean hasMoreStubs(){
        return !stubs.isEmpty();
    }

    int nodesCount() {
        return reverseCdoIdMap.size();
    }

    int reusedNodesCount() {
        return reusedNodes;
    }

    int entitiesCount() {
        return entities;
    }

    int voCount() {
        return valueObjects;
    }

    /**
     * InstanceId for Entities,
     * System.identityHashCode for ValueObjects
     */
    private Object reverseCdoIdMapKey(Cdo cdo) {
        if (cdo.getGlobalId() instanceof InstanceId) {
            return cdo.getGlobalId();
        }
        return System.identityHashCode(cdo.getWrappedCdo().get());
    }
}
