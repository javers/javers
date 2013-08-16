package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Relation between (Entity) instances
 *
 * @author bartosz walacik
 */
public abstract class Edge {
    protected Property property;
    protected List<ObjectNode> references;

    protected Edge(Property property) {
        Validate.argumentIsNotNull(property);
        this.property = property;
        this.references = new ArrayList<>();
    }

    public Property getProperty() {
        return property;
    }

    /**
     * for SingleEdge, contains zero or one element,
     * for MultiEdge, contains zero or more elements
     *
     * @return never returns null
     */
    public List<ObjectNode> getReferences(){
        return references;
    }
}
