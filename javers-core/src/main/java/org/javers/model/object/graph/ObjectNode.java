package org.javers.model.object.graph;

import com.sun.javafx.geom.Edge;
import org.javers.model.mapping.Entity;

import java.util.List;
import java.util.Set;

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
     */
    List<Edge> getEdges();
}
