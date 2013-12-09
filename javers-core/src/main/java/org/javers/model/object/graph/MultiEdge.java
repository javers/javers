package org.javers.model.object.graph;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * OneToMany or ManyToMany relation
 * @author bartosz walacik
 */
public class MultiEdge extends Edge {
    protected List<ObjectNode> inReferences;
    protected List<ObjectNode> outReferences;

    public MultiEdge(Property property) {
        super(property);
        outReferences = new ArrayList<>();
        inReferences = new ArrayList<>();
    }

    public List<GlobalCdoId> getReferencedGlobalCdoIds(Direction direction) {
        List<ObjectNode> references = getReferences(direction);

        return Lists.transform(references, new Function<ObjectNode, GlobalCdoId>() {
            @Override
            public GlobalCdoId apply(ObjectNode input) {
                return new GlobalCdoId(input.getGlobalCdoId(), input.getEntity());
            }
        });
    }

    private List<ObjectNode> getReferences(Direction direction) {
        if (direction == Direction.IN) {
            return inReferences;
        } else {
            return outReferences;
        }
    }

    public List<ObjectNode> getInReferences(){
        return Collections.unmodifiableList(inReferences);
    }

    public List<ObjectNode> getOutReferences(){
        return Collections.unmodifiableList(outReferences);
    }

    /**
     * @return null if not found
     */
    public ObjectNode getReference(Object referencedCdoId){
        for (ObjectNode ref: inReferences) {
            if (ref.getLocalCdoId().equals(referencedCdoId)) {
                return ref;
            }
        }
        return null;
    }

    public void addOutReferenceNode(ObjectNode objectNode) {
        outReferences.add(objectNode);
    }

    public void addInReferenceNode(ObjectNode objectNode) {
        inReferences.add(objectNode);
    }

    @Override
    public void accept(GraphVisitor visitor) {
        for(ObjectNode objectNode : inReferences) {
            objectNode.accept(visitor);
        }
    }
}
