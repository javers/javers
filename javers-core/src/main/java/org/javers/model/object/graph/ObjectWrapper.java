package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Wrapper for live client's domain object (aka CDO)
 *
 * @author bartosz walacik
 */
public class ObjectWrapper extends ObjectNode {
    private final Cdo cdo;
    private final Map<Property, Edge> edges;


    public ObjectWrapper(Cdo cdo) {
        argumentIsNotNull(cdo);
        this.cdo = cdo;
        this.edges = new HashMap<>();
    }

    public ObjectWrapper(Object cdo, Entity entity) {
        this(new Cdo(cdo, new InstanceId(cdo,entity)));
    }

    /**
     * @return never returns null
     */
    public Object unwrapCdo() {
        return cdo.getWrappedCdo();
    }

    @Override
    public Cdo getCdo() {
        return cdo;
    }

    @Override
    public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        return property.get(unwrapCdo());
    }

    @Override
    public GlobalCdoId getGlobalCdoId() {
        return cdo.getGlobalId();
    }

    @Override
    public ManagedClass getManagedClass() {
        return cdo.getManagedClass();
    }

    @Override
    public List<Edge> getEdges() {
        return new ArrayList<>(edges.values());
    }

    @Override
    public Edge getEdge(Property property) {
        return edges.get(property);
    }

    public void addEdge(Edge edge) {
        this.edges.put(edge.getProperty(), edge);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectWrapper that = (ObjectWrapper) o;
        return cdo.equals(that.cdo);
    }

    @Override
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
