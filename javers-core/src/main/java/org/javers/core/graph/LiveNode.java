package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.MissingProperty;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class LiveNode extends ObjectNode<LiveCdo>{

    private final Map<String, Edge> edges = new HashMap<>();

    private final Optional<LiveNode> parent;

    public LiveNode(LiveCdo cdo, Optional<LiveNode> parent) {
        super(cdo);
        this.parent = parent;
    }

    LiveNode(LiveCdo cdo) {
        this(cdo, Optional.empty());
    }

    Edge getEdge(Property property) {
        return getEdge(property.getName());
    }

    Edge getEdge(String propertyName) {
        return edges.get(propertyName);
    }

    @Override
    public boolean isEdge() {
        return false;
    }

    Optional<LiveNode> findOnPathFromRoot(Predicate<LiveNode> hitCondition, Predicate<LiveNode> stopCondition) {
        LiveNode currentNode = this;
        while (currentNode != null && !stopCondition.test(currentNode)) {
            if (hitCondition.test(currentNode)) {
                return Optional.of(currentNode);
            }
            currentNode = currentNode.parent.orElse(null);
        }
        return Optional.empty();
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
        if (propertyValue == null || propertyValue instanceof MissingProperty) {
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

    // used in tests
    Set<LiveCdo> descendants(int maxDepth) {
        return new NodeTraverser(this, maxDepth, null).descendantsList().stream().collect(Collectors.toUnmodifiableSet());
    }

    List<LiveCdo> descendantVOs(int maxDepth) {
        return new NodeTraverser(this, maxDepth,
                (LiveNode n) -> n.getGlobalId() instanceof ValueObjectId).descendantsList();
    }

    private static class NodeTraverser {
        private final Set<LiveNode> descendantsSet = new HashSet<>();
        private final List<LiveCdo> descendantsList = new ArrayList<>();
        private final int maxDepth;
        private final ObjectNode root;
        private final Predicate<LiveNode> filter;

        NodeTraverser(LiveNode root, int maxDepth, Predicate<LiveNode> filter) {
            this.maxDepth = maxDepth;
            this.root = root;
            this.filter = filter != null ? filter : (LiveNode n) -> true ;
            followEdges(root, 1);
        }

        void follow(Edge edge, int depth) {
            edge.getReferences().forEach(reference -> {
                if(!descendantsSet.contains(reference) && !reference.equals(root) && filter.test(reference)) {
                    descendantsSet.add(reference);  // for search
                    descendantsList.add(reference.getCdo()); // for consistent ordering
                    if (depth < maxDepth) {
                        followEdges(reference, depth + 1);
                    }
                }
            });
        }

        List<LiveCdo> descendantsList() {
            return Lists.immutableListOf(descendantsList);
        }

        void followEdges(LiveNode node, int depth) {
            node.edges.values().forEach(e -> follow((Edge)e, depth));
        }
    }

    boolean isEntityNode() {
        return getGlobalId() instanceof InstanceId;
    }

    boolean isValueObjectNode() {
        return getGlobalId() instanceof ValueObjectId || getGlobalId() instanceof UnboundedValueObjectId;
    }

    @Override
    public String toString() {
        return "LiveNode{" + hashCode() + ", globaId:" + getGlobalId() +
                ", edges:" +edges.size() +" }";
    }
}
