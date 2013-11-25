package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.Property;
import org.javers.model.visitors.Visitable;

/**
 * Relation between (Entity) instances
 * <br/>
 * Immutable
 *
 * @author bartosz walacik
 */
public abstract class Edge implements Visitable {
    protected final Property property;

    protected Edge(Property property) {
        Validate.argumentIsNotNull(property);
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Edge that = (Edge) obj;
        return property.equals(that.property);
    }

    @Override
    public int hashCode() {
        return property.hashCode();
    }

    //not sure if it is useful
    //public abstract Entity getReferencedEntity();
}
