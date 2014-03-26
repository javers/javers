package org.javers.core.graph;

import org.javers.common.patterns.visitors.Visitable;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoWrapper;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.ValueObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Node in client's domain objects graph. Reflects one Cdo, {@link Entity} or {@link ValueObject}
 * <p/>
 *
 * Implementation should delegate equals() and hashCode() to {@link Cdo}
 *
 * @author bartosz walacik
 */
public class ObjectNode implements Visitable<GraphVisitor> {
    private final Cdo cdo;
    private final Map<Property, Edge> edges;

    public ObjectNode(Cdo cdo) {
        argumentIsNotNull(cdo);
        this.cdo = cdo;
        this.edges = new HashMap<>();
    }


    ObjectNode(Object cdo, Entity entity) {
        this(new CdoWrapper(cdo, new InstanceId(cdo,entity)));
    }

    /**
     * @return never returns null
     */
    //Cdo getCdo();

    /**
     * @return never returns null
     */
    public Object wrappedCdo() {
        return cdo.getWrappedCdo();
    }

    /**
     * shortcut to {@link Cdo#getGlobalId()}
     */
    public GlobalCdoId getGlobalCdoId() {
        return cdo.getGlobalId();
    }

    /**
     * shortcut to {@link Cdo#getManagedClass()}
     */
    public ManagedClass getManagedClass() {
        return cdo.getManagedClass();
    }

    public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        return cdo.getPropertyValue(property);
    }

    /**
     * References to other Entities or ValueObjects
     *
     * @return never returns null
     */
    public List<Edge> getEdges() {
        return new ArrayList<>(edges.values());
    }

    public Edge getEdge(Property property) {
        return edges.get(property);
    }

    void addEdge(Edge edge) {
        this.edges.put(edge.getProperty(), edge);
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

    @Override
    public void accept(GraphVisitor visitor) {
        if(visitor.wasVisited(this)){
            return;
        }

        visitor.visit(this);

        for(Edge edge : edges.values()) {
            edge.accept(visitor);
        }
    }
}
