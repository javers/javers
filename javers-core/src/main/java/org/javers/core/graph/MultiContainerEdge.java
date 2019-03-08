package org.javers.core.graph;

import org.javers.core.metamodel.type.ContainerType;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.Collections;
import java.util.List;

/**
 * OneToMany or ManyToMany relation
 * @author bartosz walacik
 */
class MultiContainerEdge extends MultiEdge {
    private final List<LiveNode> references; //should not be empty

    MultiContainerEdge(JaversProperty property, List<LiveNode> reference) {
        super(property);
        this.references = Collections.unmodifiableList(reference);
    }

    @Override
    List<LiveNode> getReferences() {
        return references;
    }

    @Override
    Object getDehydratedPropertyValue() {
        EnumerableType enumerableType = getProperty().getType();

        if (enumerableType instanceof ContainerType) {
            return enumerableType.map(getReferences(), (input) -> {
                LiveNode liveNode = (LiveNode) input;
                return liveNode.getGlobalId();
            });
        } else {
            return null;
        }
    }
}
