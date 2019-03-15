package org.javers.core.graph;

import org.javers.core.metamodel.type.JaversProperty;

import java.util.Collections;
import java.util.List;

class ShallowMultiEdge extends AbstractMultiEdge {
    private final Object dehydratedPropertyValue;

    ShallowMultiEdge(JaversProperty property, Object dehydratedPropertyValue) {
        super(property);
        this.dehydratedPropertyValue = dehydratedPropertyValue;
    }

    @Override
    List<LiveNode> getReferences() {
        return Collections.emptyList();
    }

    @Override
    Object getDehydratedPropertyValue() {
        return dehydratedPropertyValue;
    }
}
