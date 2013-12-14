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

    private final ObjectNode reference;

    public SingleEdge(ObjectNode reference) {
        Validate.argumentIsNotNull(reference);

        this.reference = reference;
    }

    public ObjectNode getReference() {
        return reference;
    }

    @Override
    public void accept(GraphVisitor visitor) {
        reference.accept(visitor);
    }
}
