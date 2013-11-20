package org.javers.model.object.graph;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Entity;
import org.javers.model.visitors.Visitable;
import org.javers.model.visitors.Visitor;

import java.util.ArrayList;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Wrapper for live client's domain object (aka CDO),
 * captures current state of it.
 *
 * @author bartosz walacik
 */
public class ObjectWrapper implements ObjectNode {
    private final Object cdo;
    private final Entity entity;
    private final List<Edge> edges;
    private final GlobalCdoId globalCdoId;


    public ObjectWrapper(Object cdo, Entity entity) {
        argumentIsNotNull(cdo);
        argumentIsNotNull(entity);
        if (!entity.isInstance(cdo)) {
            throw new IllegalArgumentException("cdo is not an instance of entity");
        }

        this.cdo = cdo;
        this.entity = entity;
        this.edges = new ArrayList<>();
        this.globalCdoId = new GlobalCdoId(entity, getCdoId());
    }

    public Object getCdo() {
        return cdo;
    }

    @Override
    public Object getCdoId() {
        return entity.getIdProperty().get(cdo);
    }

    @Override
    public GlobalCdoId getGlobalCdoId() {
        return globalCdoId;
    }

    @Override
    public Entity getEntity() {
        return entity;
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
        return globalCdoId.equals(that.globalCdoId);
    }

    @Override
    public int hashCode() {
        return globalCdoId.hashCode();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);

        for(Edge edge : edges) {
            edge.accept(visitor);
        }
    }
}
