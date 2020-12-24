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

abstract class FakeNodePair implements NodePair {

    final ObjectNode real;

    private final Optional<CommitMetadata> commitMetadata;

    FakeNodePair(ObjectNode real, Optional<CommitMetadata> commitMetadata) {
        this.real = real;
        this.commitMetadata = commitMetadata;
    }

    @Override
    public ManagedType getManagedType() {
        return real.getManagedType();
    }

    @Override
    public boolean isNullOnBothSides(Property property) {
        return real.getPropertyValue(property) == null;
    }

    @Override
    public GlobalId getGlobalId() {
        return real.getGlobalId();
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
    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    @Override
    public PropertyChangeType getChangeType(JaversProperty property) {
        return PropertyChangeType.PROPERTY_VALUE_CHANGED;
    }

    @Override
    public ObjectNode getFirst() {
        return real;
    }
}