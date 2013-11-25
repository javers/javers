package org.javers.model.object.graph;

import org.javers.model.domain.Cdo;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Entity;
import org.javers.model.visitors.Visitable;
import org.javers.model.visitors.Visitor;

import java.util.ArrayList;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Wrapper for live client's domain object (aka CDO)
 *
 * @author bartosz walacik
 */
public class ObjectWrapper implements ObjectNode {
    private final Cdo cdo;
    private final List<Edge> edges;


    public ObjectWrapper(Cdo cdo) {
        argumentIsNotNull(cdo);
        this.cdo = cdo;
        this.edges = new ArrayList<>();
    }

    @Deprecated
    public ObjectWrapper(Object cdo, Entity entity) {
        this(new Cdo(cdo, entity));
    }

    public Object unwrapCdo() {
        return cdo.getWrappedCdo();
    }

    @Override
    public Object getLocalCdoId() {
        return cdo.getLocalId();
    }

    @Override
    public GlobalCdoId getGlobalCdoId() {
        return cdo.getGlobalId();
    }

    @Override
    public Entity getEntity() {
        return cdo.getEntity();
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
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
    public void accept(Visitor visitor) {
        visitor.visit(this);

        for(Edge edge : edges) {
            edge.accept(visitor);
        }
    }
}
