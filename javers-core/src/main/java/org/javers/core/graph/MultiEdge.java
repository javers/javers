package org.javers.core.graph;

import org.javers.core.metamodel.type.JaversProperty;

abstract class MultiEdge extends Edge {

    public MultiEdge(JaversProperty property) {
        super(property);
    }
}
