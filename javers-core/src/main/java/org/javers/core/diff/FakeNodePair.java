package org.javers.core.diff;

import org.javers.common.collections.Defaults;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.PropertyChangeType;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class FakeNodePair implements NodePair {

    private final ObjectNode right;
    private final Optional<CommitMetadata> commitMetadata;

    public FakeNodePair(ObjectNode right, Optional<CommitMetadata> commitMetadata) {
        this.right = right;
        this.commitMetadata = commitMetadata;
    }

    @Override
    public ManagedType getManagedType() {
        return right.getManagedType();
    }

    @Override
    public boolean isNullOnBothSides(Property property) {
        return right.getPropertyValue(property) == null;
    }

    @Override
    public GlobalId getGlobalId() {
        return right.getGlobalId();
    }

    @Override
    public ObjectNode getRight() {
        return right;
    }

    @Override
    public ObjectNode getLeft() {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED, "FakeNodePair.getLeft()");
    }

    @Override
    public Object getLeftDehydratedPropertyValueAndSanitize(JaversProperty property) {
        return sanitize( Defaults.defaultValue(property.getGenericType()), property.getType());
    }

    @Override
    public List<JaversProperty> getProperties() {
        return getManagedType().getProperties();
    }

    @Override
    public Object getLeftPropertyValue(Property property) {
        return Defaults.defaultValue(property.getGenericType());
    }

    @Override
    public Object getRightPropertyValue(Property property) {
        return right.getPropertyValue(property);
    }
    @Override
    public GlobalId getRightReference(Property property) {
         return right.getReference(property);
    }

    @Override
    public GlobalId getLeftReference(Property property) {
        return null;
    }

    @Override
    public List<GlobalId> getRightReferences(JaversProperty property) {
        return right.getReferences(property);
    }

    @Override
    public List<GlobalId> getLeftReferences(JaversProperty property) {
        return Collections.emptyList();
    }

    @Override
    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    @Override
    public PropertyChangeType getChangeType(JaversProperty property) {
        return PropertyChangeType.PROPERTY_VALUE_CHANGED;
    }
}