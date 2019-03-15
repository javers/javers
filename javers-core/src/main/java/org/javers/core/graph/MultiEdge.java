package org.javers.core.graph;

import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.List;

class MultiEdge extends AbstractMultiEdge {
    /**
     * This is the tricky part.
     *
     * This object holds a copy of original structure,
     * with references replaced with corresponding LiveNodes.
     *
     * Having that, it's easy to compute dehydratedPropertyValue and
     * list of referenced nodes.
     */
    private final Object nodesEnumerable;

    private List<LiveNode> memoizedReferences;
    private Object memoizedDehydratedPropertyValue;

    MultiEdge(JaversProperty property, Object nodesEnumerable) {
        super(property);
        this.nodesEnumerable = nodesEnumerable;
    }

    @Override
    Object getDehydratedPropertyValue() {
        if (memoizedDehydratedPropertyValue != null) {
            return memoizedDehydratedPropertyValue;
        }

        EnumerableType enumerableType = getProperty().getType();

        memoizedDehydratedPropertyValue = enumerableType.map(nodesEnumerable, (it) -> {
            if (it instanceof LiveNode) {
                return ((LiveNode)it).getGlobalId();
            }
            return it;
        });

        return memoizedDehydratedPropertyValue;
    }

    @Override
    List<LiveNode> getReferences() {
        if (memoizedReferences != null) {
            return memoizedReferences;
        }

        EnumerableType enumerableType = getProperty().getType();
        memoizedReferences = enumerableType.filterToList(nodesEnumerable, LiveNode.class);

        return memoizedReferences;
    }
}
