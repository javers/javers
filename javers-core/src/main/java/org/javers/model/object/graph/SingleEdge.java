package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

/**
 * OneToOne or ManyToOne relation
 * <br/>
 * Immutable
 *
 * @author bartosz walacik
 */
public class SingleEdge extends Edge {

    private final ObjectNode reference;

    public SingleEdge(Property property, ObjectNode reference) {
        super(property);
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
