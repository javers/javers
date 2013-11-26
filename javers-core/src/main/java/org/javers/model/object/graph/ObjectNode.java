package org.javers.model.object.graph;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Entity;
import org.javers.model.visitors.Visitable;

import java.util.List;

/**
 * Node in client's domain objects graph (CDO graph)
 *
 * @author bartosz walacik
 */
public interface ObjectNode extends Visitable<GraphVisitor> {

    Entity getEntity();

    Object getLocalCdoId();

    /**
     * References to other Entities
     *
     * @return never returns null
     */
    List<Edge> getEdges();

    GlobalCdoId getGlobalCdoId();
}
