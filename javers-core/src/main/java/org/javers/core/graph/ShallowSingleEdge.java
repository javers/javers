package org.javers.core.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.Collections;
import java.util.List;

/**
 * @author bartosz.walacik
 */
class ShallowSingleEdge extends AbstractSingleEdge {
    private final GlobalId reference;

    ShallowSingleEdge(JaversProperty property, GlobalId referenced) {
        super(property);
        Validate.argumentIsNotNull(referenced);
        this.reference = referenced;
    }

    @Override
    GlobalId getReference() {
        return reference;
    }

    @Override
    List<LiveNode> getReferences() {
        return Collections.emptyList();
    }
}
