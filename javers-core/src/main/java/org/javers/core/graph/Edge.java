package org.javers.core.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.JaversProperty;

/**
 * Relation between (Entity) instances
 * <br>
 * Immutable
 *
 * @author bartosz walacik
 */
abstract class Edge {
    private final JaversProperty property;

    Edge(JaversProperty property) {
        Validate.argumentIsNotNull(property);
        this.property = property;
    }

    public JaversProperty getProperty() {
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
