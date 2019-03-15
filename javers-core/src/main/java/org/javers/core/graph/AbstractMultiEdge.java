package org.javers.core.graph;

import org.javers.core.metamodel.type.JaversProperty;

abstract class AbstractMultiEdge extends Edge {
    public AbstractMultiEdge(JaversProperty property) {
        super(property);
    }
}
