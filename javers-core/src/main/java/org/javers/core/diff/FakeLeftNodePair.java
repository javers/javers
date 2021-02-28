package org.javers.core.diff;

import org.javers.common.collections.Defaults;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class FakeLeftNodePair extends FakeNodePair {

    FakeLeftNodePair(ObjectNode realRight, Optional<CommitMetadata> commitMetadata) {
        super(realRight, commitMetadata);
    }

    @Override
    public ObjectNode getRight() {
        return real;
    }

    @Override
    public Object getRightPropertyValue(Property property) {
        return real.getPropertyValue(property);
    }

    @Override
    public GlobalId getRightReference(Property property) {
        return real.getReference(property);
    }

    @Override
    public List<GlobalId> getRightReferences(JaversProperty property) {
        return real.getReferences(property);
    }

    @Override
    public ObjectNode getLeft() {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED, "FakeLeftNodePair.getLeft()");
    }

    @Override
    public List<GlobalId> getLeftReferences(JaversProperty property) {
        return Collections.emptyList();
    }

    @Override
    public Object getLeftPropertyValue(Property property) {
        return Defaults.defaultValue(property.getGenericType());
    }

    @Override
    public GlobalId getLeftReference(Property property) {
        return null;
    }

    @Override
    public Object getLeftDehydratedPropertyValue(JaversProperty property) {
        return Defaults.defaultValue(property.getGenericType());
    }
}
