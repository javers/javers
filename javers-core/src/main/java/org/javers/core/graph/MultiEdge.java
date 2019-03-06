package org.javers.core.graph;

import org.javers.core.metamodel.type.JaversProperty;

import java.util.Collections;
import java.util.List;

/**
 * OneToMany or ManyToMany relation
 * @author bartosz walacik
 */
class MultiEdge extends Edge {
    private final List<LiveNode> references; //should not be empty

    MultiEdge(JaversProperty property, List<LiveNode> reference) {
        super(property);
        this.references = Collections.unmodifiableList(reference);
    }

    @Override
    List<LiveNode> getReferences(){
        return references;
    }
}
