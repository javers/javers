package org.javers.core.graph;

import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;

import java.util.*;

/**
 * @author bartosz walacik
 */
class NodeReuser {
    private final Map<Object, LiveNode> reverseCdoIdMap = new HashMap<>();
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

    LiveNode getForReuse(Cdo cdo) {
        reusedNodes++;
        return reverseCdoIdMap.get(reverseCdoIdMapKey(cdo));
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

    /**
     * InstanceId for Entities,
     * System.identityHashCode for ValueObjects
     */
    private Object reverseCdoIdMapKey(Cdo cdo) {
        if (cdo.getGlobalId() instanceof InstanceId) {
            return cdo.getGlobalId();
        }
        return new SystemIdentityWrapper(cdo.getWrappedCdo().get());
    }

    private static class SystemIdentityWrapper {
        private final Object cdo;

        SystemIdentityWrapper(Object cdo) {
            this.cdo = cdo;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != SystemIdentityWrapper.class) {
                return false;
            }

            return this.cdo == ((SystemIdentityWrapper)o).cdo;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(cdo);
        }

        @Override
        public String toString() {
            return "SystemIdentityWrapper{" +
                    "hash:"+hashCode()+", "+
                    "cdo:" + cdo +
                    '}';
        }
    }
}
