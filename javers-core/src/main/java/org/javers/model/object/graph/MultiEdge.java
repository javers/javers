package org.javers.model.object.graph;

import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * OneToMany or ManyToMany relation
 * @author bartosz walacik
 */
public class MultiEdge extends Edge {
    protected List<ObjectNode> references; //should not be empty

    public MultiEdge(Property property) {
        super(property);
        references = new ArrayList<>();
    }

    public List<ObjectNode> getReferences(){
        return Collections.unmodifiableList(references);
    }

    public void addReferenceNode(ObjectNode objectNode) {
        references.add(objectNode);
    }

    @Override
    public void accept(GraphVisitor visitor) {
        for(ObjectNode objectNode : references) {
            objectNode.accept(visitor);
        }
    }
}
