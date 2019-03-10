package org.javers.core.graph;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.JaversProperty;

abstract class AbstractSingleEdge extends Edge {
    AbstractSingleEdge(JaversProperty property) {
        super(property);
    }

    abstract GlobalId getReference();

    public Object getDehydratedPropertyValue() {
        return getReference();
    }
}
