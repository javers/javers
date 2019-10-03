package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class LiveNode extends ObjectNode<LiveCdo>{

    private final Map<String, Edge> edges = new HashMap<>();

    public LiveNode(LiveCdo cdo) {
        super(cdo);
    }

    Edge getEdge(Property property) {
        return getEdge(property.getName());
    }

    Edge getEdge(String propertyName) {
        return edges.get(propertyName);
    }

    @Override
    public GlobalId getReference(Property property){
        Edge edge = getEdge(property);

        if (edge instanceof AbstractSingleEdge){
            return ((AbstractSingleEdge)edge).getReference();
        }
        else {
            //when user's class is refactored, a property can have different type
            return null;
        }
    }

    @Override
    public List<GlobalId> getReferences(JaversProperty property) {
        Edge edge = getEdge(property); //could be null for snapshots

        if (edge != null){
            return edge.getReferences()
                    .stream()
                    .map(it-> it.getGlobalId())
                    .collect(toList());
        } else {
            //when user's class is refactored, a collection can contain different items
            return Collections.emptyList();
        }
    }

    @Override
    protected Object getDehydratedPropertyValue(String propertyName) {
        return getManagedType().findProperty(propertyName)
                .map(p -> getDehydratedPropertyValue(p))
                .orElse(null);
    }

    /**
     * Enumerables are copied to new structures (immutable when possible)
     */
    @Override
    public Object getDehydratedPropertyValue(JaversProperty property) {
        Edge edge = getEdge(property);

        if (edge != null) {
            return edge.getDehydratedPropertyValue();
        }

        Object propertyValue = getCdo().getPropertyValue(property);
        if (propertyValue == null) {
            return null;
        }

        //Collections & Maps are copied to a new immutable structure
        if (property.getType() instanceof EnumerableType) {
            EnumerableType enumerableType = property.getType();

            return enumerableType.map(propertyValue, it -> it);
        }

        return getCdo().getPropertyValue(property);
    }

    void addEdge(Edge edge) {
        this.edges.put(edge.getProperty().getName(), edge);
    }

    List<LiveCdo> descendants(int maxDepth) {
        return Lists.immutableListOf(new NodeTraverser(this, maxDepth).descendants);
    }

    private static class NodeTraverser {
        private final Set<LiveCdo> descendants = new HashSet();
        private final int maxDepth;
        private final ObjectNode root;

        NodeTraverser(LiveNode root, int maxDepth) {
            this.maxDepth = maxDepth;
            this.root = root;
            followEdges(root, 1);
        }

        void follow(Edge edge, int depth) {
            edge.getReferences().forEach(n -> {
                if(!descendants.contains(n.getCdo()) && !n.equals(root)) {
                    descendants.add(n.getCdo());
                    if (depth < maxDepth) {
                        followEdges(n, depth + 1);
                    }
                }
            });
        }

        void followEdges(LiveNode node, int depth) {
            node.edges.values().forEach(e -> follow((Edge)e, depth));
        }
    }

    @Override
    public String toString() {
        return "LiveNode{" + hashCode() + ", globaId:" + getGlobalId() +
                ", edges:" +edges.size() +" }";
    }
}
