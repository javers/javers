package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

public class LiveNode extends ObjectNode<LiveCdo>{

    private final Map<JaversProperty, Edge> edges = new HashMap<>();

    public LiveNode(LiveCdo cdo) {
        super(cdo);
    }

    Edge getEdge(Property property) {
        return edges.get(property);
    }

    Edge getEdge(String propertyName) {
        for (JaversProperty p :  edges.keySet()){
            if (p.getName().equals(propertyName)){
                return getEdge(p);
            }
        }
        return null;
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
    public Collection<GlobalId> getReferences(Property property) {
        Edge edge = getEdge(property); //could be null for snapshots

        if (edge instanceof MultiEdge){
            return unmodifiableList(edge.getReferences()
                    .stream()
                    .map(it-> it.getGlobalId())
                    .collect(Collectors.toList()));
        } else {
            //when user's class is refactored, a collection can contain different items
            return Collections.emptyList();
        }
    }

    @Override
    public Object getDehydratedPropertyValue(String property) {
        Edge edge = getEdge(property);
        if (edge == null) {
            return getCdo().getPropertyValue(property);
        }

        if (edge instanceof AbstractSingleEdge) {
            return ((AbstractSingleEdge)edge).getReference();
        } else {
            MultiEdge multiEdge = (MultiEdge)edge;
            EnumerableType enumerableType = multiEdge.getProperty().getType();

            return enumerableType.map(multiEdge.getReferences(), (input) -> {
                LiveNode liveNode = (LiveNode)input;
                return liveNode.getGlobalId();
            });
        }
    }

    void addEdge(Edge edge) {
        this.edges.put(edge.getProperty(), edge);
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
}
