package org.javers.model.object.graph;

import org.javers.model.domain.Cdo;
import org.javers.model.domain.GlobalCdoId;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.ValueObject;
import org.javers.model.visitors.Visitable;

import java.util.List;

/**
 * Node in client's domain objects graph. Reflects one Cdo, {@link Entity} or {@link ValueObject}
 * <p/>
 *
 * Implementation should delegate equals() and hashCode() to {@link Cdo}
 *
 * @author bartosz walacik
 */
public abstract class ObjectNode implements Visitable<GraphVisitor> {

    /**
     * @return never returns null
     */
    public abstract Cdo getCdo();

    /**
     * shortcut to {@link Cdo#getManagedClass()}
     */
    public abstract ManagedClass getManagedClass();

    /**
     * shortcut to {@link Cdo#getGlobalId()}
     */
    public abstract GlobalCdoId getGlobalCdoId();

    public abstract Object getPropertyValue(Property property);

    /**
     * References to other Entities
     *
     * @return never returns null
     */
    public abstract List<Edge> getEdges();

    public abstract Edge getEdge(Property property);
}
