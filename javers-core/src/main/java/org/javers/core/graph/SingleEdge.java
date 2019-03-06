package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.List;

/**
 * OneToOne or ManyToOne relation
 * <br>
 * Immutable
 *
 * @author bartosz walacik
 */
class SingleEdge extends AbstractSingleEdge {
    private final LiveNode referencedNode;

    SingleEdge(JaversProperty property, LiveNode referencedNode) {
        super(property);
        Validate.argumentsAreNotNull(referencedNode);
        this.referencedNode = referencedNode;
    }

    @Override
    GlobalId getReference() {
        return referencedNode.getGlobalId();
    }

    @Override
    List<LiveNode> getReferences() {
        return Lists.asList(referencedNode);
    }
}
