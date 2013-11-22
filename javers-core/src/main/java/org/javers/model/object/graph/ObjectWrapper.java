package org.javers.model.object.graph;

import org.javers.model.domain.Cdo;
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

    //TODO change name to getCdoLocalId
    @Override
    public Object getCdoId() {
        return cdo.getLocalId();
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
}
