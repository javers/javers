package org.javers.model.object.graph;

import org.javers.model.mapping.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * OneToMany or ManyToMany relation
 * @author bartosz walacik
 */
public class MultiEdge extends Edge {
    protected List<ObjectNode> references;

    public MultiEdge(Property property) {
        super(property);
        references = new ArrayList<>();
    }

    public List<ObjectNode> getReferences(){
        return Collections.unmodifiableList(references);
    }

    /**
     * @return null if not found
     */
    public ObjectNode getReference(Object referencedCdoId){
        for (ObjectNode ref: references) {
            if (ref.getCdoId().equals(referencedCdoId)) {
                return ref;
            }
        }
        return null;
    }


    public void addReferenceNode(ObjectNode objectNode) {
        references.add(objectNode);
    }
}
