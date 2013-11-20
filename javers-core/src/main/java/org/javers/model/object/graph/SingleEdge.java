package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.Property;
import org.javers.model.visitors.Visitor;

/**
 * OneToOne or ManyToOne relation
 * <br/>
 * Immutable
 *
 * @author bartosz walacik
 */
public class SingleEdge extends Edge {

    private final ObjectNode reference;

    public SingleEdge(Property property, ObjectNode referenceTarget) {
        super(property);
        Validate.argumentIsNotNull(referenceTarget);

        this.reference = referenceTarget;
    }

    public ObjectNode getReference() {
        return reference;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        reference.accept(visitor);
    }
}
