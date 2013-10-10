package org.javers.model.object.graph;

import org.javers.model.mapping.Entity;

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


    public ObjectWrapper(Object cdo, Entity entity) {
        argumentIsNotNull(cdo);
        argumentIsNotNull(entity);
        if (!entity.isInstance(cdo)){
            throw new IllegalArgumentException("cdo is not an instance of entity");
        }

        this.cdo = cdo;
        this.entity = entity;
        this.edges = new ArrayList<>();
    }

    public Object getCdo() {
        return cdo;
    }

    @Override
    public Object getCdoId() {
        return entity.getIdProperty().get(cdo);
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
}
