package org.javers.model.object.graph;

import org.javers.model.mapping.Property;

import java.util.List;
/**
 * OneToMany or ManyToMany relation
 * @author bartosz walacik
 */
public class MultiEdge extends Edge {
    public MultiEdge(Property property) {
        super(property);
    }
}
