package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Node in client's domain object graph. Reflects one {@link Cdo} or {@link CdoSnapshot}.
 * <p/>
 * Cdo could be an {@link EntityType} or a {@link ValueObjectType}
 * <p/>
 * Implementation should delegate equals() and hashCode() to {@link Cdo}
 *
 * @author bartosz walacik
 */
public class ObjectNode<T extends Cdo> {
    private final T cdo;
    private final Map<JaversProperty, Edge> edges = new HashMap<>();

    public ObjectNode(T cdo) {
        argumentsAreNotNull(cdo);
        this.cdo = cdo;
    }

    /**
     * @return returns {@link Optional#empty()} for snapshots
     */
    public Optional<Object> wrappedCdo() {
        return cdo.getWrappedCdo();
    }

    /**
     * shortcut to {@link Cdo#getGlobalId()}
     */
    public GlobalId getGlobalId() {
        return cdo.getGlobalId();
    }

    /**
     * returns null if property is not ManagedType
     */
    public GlobalId getReference(Property property){
        Edge edge = getEdge(property); //could be null for snapshots

        //TODO this is ugly, how to move this logic to Cdo implementations?
        if (edge instanceof AbstractSingleEdge){
            return ((AbstractSingleEdge)edge).getReference();
        }
        else {
            Object propertyValue = getPropertyValue(property);
            if (propertyValue instanceof GlobalId) {
                return (GlobalId)propertyValue;
            } else {
                //when user's class is refactored, a property can have different type
                return null;
            }
        }
    }

    /**
     * returns null if property is not Collection of ManagedType
     */
    public Collection<GlobalId> getReferences(Property property) {
        Edge edge = getEdge(property); //could be null for snapshots

        if (edge instanceof MultiEdge){
            return unmodifiableList(edge.getReferences()
                        .stream()
                        .map(it-> it.getGlobalId())
                        .collect(Collectors.toList()));
        }

        Object propertyValue = getPropertyValue(property);
        if (propertyValue == null || !(propertyValue instanceof Collection)) {
            return Collections.emptyList();
        }

        Collection collection = (Collection) propertyValue;
        if (collection.size() == 0) {
            return Collections.emptyList();
        }

        Object firstItem = collection.iterator().next();
        if (firstItem instanceof GlobalId) {
            return collection;
        } else {
            //when user's class is refactored, a collection can contain different items
            return Collections.emptyList();
        }
    }

    public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        return cdo.getPropertyValue(property);
    }

    public boolean isNull(Property property){
        return cdo.isNull(property);
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

    void addEdge(Edge edge) {
        this.edges.put(edge.getProperty(), edge);
    }

    public ManagedType getManagedType() {
        return cdo.getManagedType();
    }

    public T getCdo() {
        return cdo;
    }

    List<T> descendants(int maxDepth) {
        return Lists.immutableListOf(new NodeTraverser(this, maxDepth).descendants);
    }

    private static class NodeTraverser {
        private final Set<Cdo> descendants = new HashSet();
        private final int maxDepth;
        private final ObjectNode root;

        NodeTraverser(ObjectNode root, int maxDepth) {
            this.maxDepth = maxDepth;
            this.root = root;
            followEdges(root, 1);
        }

        void follow(Edge edge, int depth) {
            edge.getReferences().forEach(n -> {
               if(!descendants.contains(n.cdo) && !n.equals(root)) {
                   descendants.add(n.cdo);
                   if (depth < maxDepth) {
                       followEdges(n, depth + 1);
                   }
               }
            });
        }

        void followEdges(ObjectNode node, int depth) {
            node.edges.values().forEach(e -> follow((Edge)e, depth));
        }
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectNode that = (ObjectNode) o;
        return cdo.equals(that.cdo);
    }

    public int hashCode() {
        return cdo.hashCode();
    }
}