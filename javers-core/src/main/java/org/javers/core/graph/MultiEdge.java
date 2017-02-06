package org.javers.core.graph;

import org.javers.core.metamodel.type.JaversProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * OneToMany or ManyToMany relation
 * @author bartosz walacik
 */
class MultiEdge extends Edge {
    private final List<ObjectNode> references; //should not be empty

    public MultiEdge(JaversProperty property) {
        super(property);
        references = new ArrayList<>();
    }

    public List<ObjectNode> getReferences(){
        return Collections.unmodifiableList(references);
    }

    public void addReferenceNode(ObjectNode objectNode) {
        references.add(objectNode);
    }
}
