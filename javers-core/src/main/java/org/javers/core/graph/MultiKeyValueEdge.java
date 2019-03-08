package org.javers.core.graph;

import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.KeyValueType;

import java.util.List;

class MultiKeyValueEdge extends MultiEdge {
    // TODO
    // Map<LiveNode>
    private final Object nodesMap;


    MultiKeyValueEdge(JaversProperty property, Object nodesMap) {
        super(property);
        this.nodesMap = nodesMap;
    }

    @Override
    Object getDehydratedPropertyValue() {
        KeyValueType keyValueType = getProperty().getType();

        return keyValueType.map(nodesMap, (it) -> {
            if (it instanceof LiveNode) {
                return ((LiveNode)it).getGlobalId();
            }
            return it;
        });
    }

    @Override
    List<LiveNode> getReferences() {
        KeyValueType keyValueType = getProperty().getType();

        return keyValueType.mapToList(nodesMap, (it) -> {
           if (it instanceof LiveNode) {
               return it;
           }
           return null;
        });
    }
}
