package org.javers.model.object.graph;

import org.javers.model.domain.Cdo;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.mapping.Property;

import java.util.Collections;
import java.util.List;

public class Fake extends ObjectNode{

    private GlobalCdoId id;

    public Fake(GlobalCdoId id) {
        this.id = id;
    }

    @Override
    public Cdo getCdo() {
        return null;
    }

    @Override
    public ManagedClass getManagedClass() {
        return id.getCdoClass();
    }

    @Override
    public GlobalCdoId getGlobalCdoId() {
        return id;
    }

    @Override
    public Object getPropertyValue(Property property) {
        return null;
    }

    @Override
    public List<Edge> getEdges() {
        return Collections.emptyList();
    }

    @Override
    public Edge getEdge(Property property) {
        return null;
    }

    @Override
    public void accept(GraphVisitor visitor) {
        if(visitor.wasVisited(this)){
            return;
        }

        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectNode that = (ObjectNode) o;
        return id.equals(that.getGlobalCdoId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
