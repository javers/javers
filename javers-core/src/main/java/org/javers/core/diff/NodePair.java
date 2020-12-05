package org.javers.core.diff;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.changetype.PropertyChangeType;
import org.javers.core.graph.LiveNode;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.MissingProperty;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.*;

public interface NodePair {
    boolean isNullOnBothSides(Property property);

    GlobalId getGlobalId();

    ObjectNode getRight();

    ObjectNode getLeft();

    List<JaversProperty> getProperties();

    Object getLeftPropertyValue(Property property);

    Object getRightPropertyValue(Property property);

    GlobalId getRightReference(Property property);

    GlobalId getLeftReference(Property property);

    List<GlobalId> getRightReferences(JaversProperty property);

    List<GlobalId> getLeftReferences(JaversProperty property);

    ManagedType getManagedType();

    default Object getRightDehydratedPropertyValueAndSanitize(JaversProperty property) {
        return sanitize(getRight().getDehydratedPropertyValue(property), property.getType());
    }

    default Object getLeftDehydratedPropertyValueAndSanitize(JaversProperty property) {
        return sanitize(getLeft().getDehydratedPropertyValue(property), property.getType());
    }

    default Object sanitize(Object value, JaversType expectedType) {
        //all Enumerables (except Arrays) are sanitized
        if (expectedType instanceof EnumerableType && !(expectedType instanceof ArrayType)) {
            EnumerableType enumerableType = (EnumerableType)expectedType;
            if (value == null || !enumerableType.getEnumerableInterface().isAssignableFrom(value.getClass())) {
                return ((EnumerableType)expectedType).empty();
            }
        }
        return value;
    }

    Optional<CommitMetadata> getCommitMetadata();

    default PropertyChangeMetadata createPropertyChangeMetadata(JaversProperty property) {
        return new PropertyChangeMetadata(getGlobalId(), property.getName(), getCommitMetadata(), getChangeType(property));
    }

    PropertyChangeType getChangeType(JaversProperty property);
}
