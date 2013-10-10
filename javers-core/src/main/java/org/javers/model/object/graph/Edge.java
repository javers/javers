package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.Property;
import org.javers.model.object.graph.visitors.EdgeVisitor;
import org.javers.model.visitors.Visitable;

import java.util.ArrayList;
import java.util.List;

/**
 * Relation between (Entity) instances
 * <br/>
 * Immutable
 *
 * @author bartosz walacik
 */
public abstract class Edge implements Visitable<EdgeVisitor>{
    protected final Property property;

    protected Edge(Property property) {
        Validate.argumentIsNotNull(property);
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }
}
