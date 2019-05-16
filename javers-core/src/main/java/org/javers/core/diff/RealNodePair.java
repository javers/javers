package org.javers.core.diff;

import com.google.common.collect.Streams;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.PropertyChangeType;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.MissingProperty;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * holds two versions of the same {@link ObjectNode}
 *
 * @author bartosz walacik
 */
public class RealNodePair implements NodePair {
    private final ObjectNode left;
    private final ObjectNode right;
    private final Optional<CommitMetadata> commitMetadata;

    RealNodePair(ObjectNode left, ObjectNode right) {
        this(left, right, Optional.empty());
    }

    public RealNodePair(ObjectNode left, ObjectNode right, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(left, right, commitMetadata);
        Validate.argumentCheck(left.getGlobalId().equals(right.getGlobalId()), "left & right should refer to the same Cdo");
        this.left = left;
        this.right = right;
        this.commitMetadata = commitMetadata;
    }

    @Override
    public ManagedType getManagedType() {
        return right.getManagedType();
    }

    @Override
    public boolean isNullOnBothSides(Property property) {
        return left.getPropertyValue(property) == null &&
                right.getPropertyValue(property) == null;
    }

    @Override
    public Object getLeftPropertyValue(Property property) {
        return left.getPropertyValue(property);
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
        return left.getReference(property);
    }

    @Override
    public List<GlobalId> getRightReferences(JaversProperty property) {
        return right.getReferences(property);
    }

    @Override
    public List<GlobalId> getLeftReferences(JaversProperty property) {
        return left.getReferences(property);
    }

    @Override
    public ObjectNode getRight() {
        return right;
    }

    @Override
    public ObjectNode getLeft() {
        return left;
    }

    boolean sameClass() {
        return right.getManagedType().getBaseJavaType() == left.getManagedType().getBaseJavaType();
    }

    @Override
    public List<JaversProperty> getProperties() {
        if (sameClass()) {
            return getManagedType().getProperties();
        }
        else {
            return Collections.unmodifiableList(getPropertiesFromBothSides());
        }
    }

    private List<JaversProperty> getPropertiesFromBothSides() {
        Set<String> leftNames = left.getManagedType().getProperties().stream()
                .map(it -> it.getName()).collect(Collectors.toSet());


        return Streams.concat(left.getManagedType().getProperties().stream(),
                              right.getManagedType().getProperties().stream().filter(it -> !leftNames.contains(it.getName())))
                       .collect(Collectors.toList());
    }

    @Override
    public GlobalId getGlobalId() {
        return left.getGlobalId();
    }

    @Override
    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    public PropertyChangeType getChangeType(JaversProperty property) {
        if (getLeft().getManagedType().getBaseJavaClass() == getRight().getManagedType().getBaseJavaClass()) {
            return PropertyChangeType.PROPERTY_VALUE_CHANGED;
        }

        if (getLeftPropertyValue(property) == MissingProperty.INSTANCE) {
            return PropertyChangeType.PROPERTY_ADDED;
        }

        if (getRightPropertyValue(property) == MissingProperty.INSTANCE) {
            return PropertyChangeType.PROPERTY_REMOVED;
        }

        return PropertyChangeType.PROPERTY_VALUE_CHANGED;
    }
}