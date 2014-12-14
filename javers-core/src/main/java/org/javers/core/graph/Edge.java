package org.javers.core.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;
import org.javers.common.patterns.visitors.Visitable;

/**
 * Relation between (Entity) instances
 * <br>
 * Immutable
 *
 * @author bartosz walacik
 */
abstract class Edge {
    private final Property property;

    Edge(Property property) {
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
}
