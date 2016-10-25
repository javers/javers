package org.javers.core.graph;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

abstract class AbstractSingleEdge extends Edge {
    AbstractSingleEdge(Property property) {
        super(property);
    }

    abstract GlobalId getReference() ;
}
