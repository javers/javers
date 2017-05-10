package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<T> cdos() {
        return nodes().stream().map(node -> (T) node.getCdo()).collect(Collectors.toSet());
    }

    public Set<GlobalId> globalIds() {
        return nodes().stream().map(ObjectNode::getGlobalId).collect(Collectors.toSet());
    }

    public Optional<T> get(GlobalId globalId) {
        return nodes.stream()
            .filter(node -> globalId.equals(node.getGlobalId()))
            .findFirst()
            .map(node -> (T) node.getCdo());
    }
}
