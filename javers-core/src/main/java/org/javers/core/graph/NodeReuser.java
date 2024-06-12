package org.javers.core.graph;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;

import java.util.*;

/**
 * @author bartosz walacik
 */
class NodeReuser {
    private final Map<GlobalId, LiveNode> reverseCdoIdMapForGlobalReuse = new HashMap<>();
    private final List<LiveNode> nodes = new ArrayList<>();
    private final Queue<LiveNode> stubs = new LinkedList<>();
    private int reusedNodes;
    private int entities;
    private int valueObjects;

    NodeReuser() {
    }

    boolean isGraphLevelReusable(Cdo cdo) {
        return reverseCdoIdMapForGlobalReuse.containsKey(reverseCdoIdMapKey(cdo));
    }

    Optional<LiveNode> locallyReusableValueObjectNode(Cdo cdo, LiveNode parent) {
        return parent.findOnPathFromRoot(
                p -> p.getCdo().getWrappedCdo().equals(cdo.getWrappedCdo()),
                p -> !p.isValueObjectNode()
        );
    }

    LiveNode getForReuse(Cdo cdo) {
        reusedNodes++;
        return reverseCdoIdMapForGlobalReuse.get(reverseCdoIdMapKey(cdo));
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

        if (reference.isEntityNode()) {
            reverseCdoIdMapForGlobalReuse.put(reverseCdoIdMapKey(reference.getCdo()), reference);
        }

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
        return nodes.size();
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

    @Deprecated
    private static class SystemIdentityPlusOwnerIdWrapper {
        private final Object wrappedObject;
        private final GlobalId masterObjectId;

        SystemIdentityPlusOwnerIdWrapper(Cdo cdo) {
            this.wrappedObject = cdo.getWrappedCdo().get();
            this.masterObjectId = cdo.getGlobalId().masterObjectId();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SystemIdentityPlusOwnerIdWrapper that = (SystemIdentityPlusOwnerIdWrapper) o;
            return Objects.equals(wrappedObject, that.wrappedObject) && Objects.equals(masterObjectId, that.masterObjectId);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(wrappedObject) + masterObjectId.hashCode();
        }
    }
}
