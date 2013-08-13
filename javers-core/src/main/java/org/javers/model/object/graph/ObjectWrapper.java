package org.javers.model.object.graph;

import com.sun.javafx.geom.*;
import com.sun.javafx.geom.Edge;
import org.javers.model.mapping.Entity;

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

    public ObjectWrapper(Object cdo, Entity entity) {
        argumentIsNotNull(cdo);
        argumentIsNotNull(entity);
        if (!entity.isInstance(cdo)){
            throw new IllegalArgumentException("cdo is not an instance of entity");
        }

        this.cdo = cdo;
        this.entity = entity;
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
        return null;
    }
}
