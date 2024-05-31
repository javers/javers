package org.javers.core.graph;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;

import java.util.*;

/**
 * @author bartosz walacik
 */
class NodeReuser {
    private final Map<GlobalId, LiveNode> reverseCdoIdMap = new HashMap<>();
    private final Map<SystemIdentityWrapper, LiveNode> traversedObjects = new HashMap<>();
    private final List<LiveNode> nodes = new ArrayList<>();
    private final Queue<LiveNode> stubs = new LinkedList<>();
    private int reusedNodes;
    private int entities;
    private int valueObjects;

    NodeReuser() {
    }

    boolean isReusable(Cdo cdo) {
        return reverseCdoIdMap.containsKey(reverseCdoIdMapKey(cdo));
    }

    boolean isTraversed(Cdo cdo) {
        return traversedObjects.containsKey(traversedObjectsMapKey(cdo));
    }

    LiveNode getForReuse(Cdo cdo) {
        reusedNodes++;
        return reverseCdoIdMap.get(reverseCdoIdMapKey(cdo));
    }

    LiveNode getForDouble(Cdo cdo) {
        reusedNodes++;
        return traversedObjects.get(traversedObjectsMapKey(cdo));
    }


    List<LiveNode> nodes() {
        return Collections.unmodifiableList(nodes);
    }

    void saveForReuse(LiveNode reference) {
        if (reference.getGlobalId() instanceof InstanceId) {
            entities++;
        }
        if (reference.getGlobalId() instanceof ValueObjectId) {
            valueObjects++;
        }
        reverseCdoIdMap.put(reverseCdoIdMapKey(reference.getCdo()), reference);
        traversedObjects.put(traversedObjectsMapKey(reference.getCdo()), reference);

        nodes.add(reference);
    }

    void saveNodeDouble(LiveNode reference) {
        nodes.add(reference);
    }

    void enqueueStub(LiveNode nodeStub) {
        stubs.offer(nodeStub);
    }

    LiveNode pollStub(){
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

    private GlobalId reverseCdoIdMapKey(Cdo cdo) {
        return cdo.getGlobalId();
    }

    private SystemIdentityWrapper traversedObjectsMapKey(Cdo cdo) {
        return new SystemIdentityWrapper(cdo);
    }

    private static class SystemIdentityWrapper {
        private final Object wrappedObject;

        SystemIdentityWrapper(Cdo cdo) {
            this.wrappedObject = cdo.getWrappedCdo().get();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != SystemIdentityWrapper.class) {
                return false;
            }

            return this.wrappedObject == ((SystemIdentityWrapper)o).wrappedObject;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(wrappedObject);
        }
    }
}
