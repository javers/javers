package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

/**
 * OneToOne or ManyToOne relation
 * <br/>
 * Immutable
 *
 * @author bartosz walacik
 */
public class SingleEdge extends Edge {

    private final ObjectNode inReference;
    private final ObjectNode outReference;

    public SingleEdge(Property property, ObjectNode outReference, ObjectNode inReference) {
        super(property);
        Validate.argumentIsNotNull(outReference);
        Validate.argumentIsNotNull(inReference);

        this.outReference = outReference;
        this.inReference = inReference;
    }

    public ObjectNode getInReference() {
        return inReference;
    }

    @Override
    public void accept(GraphVisitor visitor) {
        inReference.accept(visitor);
    }

    public GlobalCdoId getReferencedGlobalCdoId(Direction direction) {
        if (direction == Direction.IN) {
            return inReference.getGlobalCdoId();
        } else {
            return outReference.getGlobalCdoId();
        }
    }
}
