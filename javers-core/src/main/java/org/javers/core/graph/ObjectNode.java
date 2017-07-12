package org.javers.core.graph;

import java.util.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;

import java.util.HashMap;
import java.util.Map;
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
public class ObjectNode {
    private final Cdo cdo;
    private final Map<JaversProperty, Edge> edges = new HashMap<>();

    public ObjectNode(Cdo cdo) {
        argumentsAreNotNull(cdo);
        this.cdo = cdo;
    }

    /**
     * @return returns {@link Optional#EMPTY} for snapshots
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
     * only for properties with return type: ManagedType
     */
    public GlobalId getReference(Property property){
        Edge edge = getEdge(property); //could be null for snapshots

        //TODO this is ugly, how to move this logic to Cdo implementations?
        if (edge instanceof AbstractSingleEdge){
            return ((AbstractSingleEdge)edge).getReference();
        }
        else {
            return (GlobalId)getPropertyValue(property);
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

    public Cdo getCdo() {
        return cdo;
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