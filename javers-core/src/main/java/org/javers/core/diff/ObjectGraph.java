package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Optional;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public abstract class ObjectGraph<T extends Cdo> {
    private final Set<ObjectNode> nodes;

    public ObjectGraph(Set<ObjectNode> nodes) {
        this.nodes = nodes;
    }

    public Set<ObjectNode> nodes() {
        return nodes;
    }

    public boolean contains(ObjectNode node){
        return nodes.contains(node);
    }

    public Optional<T> get(GlobalId globalId) {
        for (ObjectNode node : nodes){
            if (globalId.equals(node.getGlobalId())){
                return Optional.of((T) node.getCdo());
            }
        }
        return Optional.empty();
    }
}
