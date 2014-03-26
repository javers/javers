package org.javers.model.object.graph;

import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.ValueObject;
import org.javers.common.patterns.visitors.Visitable;

import java.util.List;

/**
 * Node in client's domain objects graph. Reflects one Cdo, {@link Entity} or {@link ValueObject}
 * <p/>
 *
 * Implementation should delegate equals() and hashCode() to {@link Cdo}
 *
 * @author bartosz walacik
 */
public interface ObjectNode extends Visitable<GraphVisitor> {

    /**
     * @return never returns null
     */
    Cdo getCdo();

    /**
     * shortcut to {@link Cdo#getManagedClass()}
     */
   ManagedClass getManagedClass();

    /**
     * shortcut to {@link Cdo#getGlobalId()}
     */
    GlobalCdoId getGlobalCdoId();

    Object getPropertyValue(Property property);

    /**
     * References to other Entities or ValueObjects
     *
     * @return never returns null
     */
    List<Edge> getEdges();

    Edge getEdge(Property property);
}
