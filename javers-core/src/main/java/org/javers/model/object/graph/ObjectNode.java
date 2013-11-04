package org.javers.model.object.graph;

import org.javers.model.mapping.Entity;

import java.util.List;

/**
 * Node in client's domain objects graph (CDO graph)
 *
 * @author bartosz walacik
 */
public interface ObjectNode {

    Entity getEntity();

    Object getCdoId();

    /**
     * References to other Entities
     *
     * @return never returns null
     */
    List<Edge> getEdges();
}
