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

class FakeRightNodePair extends FakeNodePair {

    FakeRightNodePair(ObjectNode realLeft, Optional<CommitMetadata> commitMetadata) {
        super(realLeft, commitMetadata);
    }

    @Override
    public ObjectNode getLeft() {
        return real;
    }

    @Override
    public Object getLeftPropertyValue(Property property) {
        return real.getPropertyValue(property);
    }

    @Override
    public GlobalId getLeftReference(Property property) {
        return real.getReference(property);
    }

    @Override
    public List<GlobalId> getLeftReferences(JaversProperty property) {
        return real.getReferences(property);
    }

    @Override
    public ObjectNode getRight() {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED, "FakeRightNodePair.getRight()");
    }

    @Override
    public List<GlobalId> getRightReferences(JaversProperty property) {
        return Collections.emptyList();
    }

    @Override
    public Object getRightPropertyValue(Property property) {
        return Defaults.defaultValue(property.getGenericType());
    }

    @Override
    public GlobalId getRightReference(Property property) {
        return null;
    }

    @Override
    public Object getRightDehydratedPropertyValue(JaversProperty property) {
        return Defaults.defaultValue(property.getGenericType());
    }
}
