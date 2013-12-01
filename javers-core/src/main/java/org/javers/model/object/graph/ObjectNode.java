package org.javers.model.object.graph;

import org.javers.model.domain.Cdo;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.Property;
import org.javers.model.visitors.Visitable;

import java.util.List;

/**
 * Node in client's domain objects graph (CDO graph)
 * <br/><br/>
 *
 * Implementation should delegate equals() and hashCode() to {@link Cdo}
 *
 * @author bartosz walacik
 */
public interface ObjectNode extends Visitable<GraphVisitor> {

    Entity getEntity();

    Object getLocalCdoId();

    Object getPropertyValue(Property property);

    /**
     * References to other Entities
     *
     * @return never returns null
     */
    List<Edge> getEdges();

    /**
     * @return never returns null
     */
    GlobalCdoId getGlobalCdoId();

    /**
     * @return never returns null
     */
    public Cdo getCdo();
}
