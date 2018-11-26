package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.*;

public interface NodePair {
    boolean isNullOnBothSides(Property property);

    GlobalId getGlobalId();

    ObjectNode getRight();

    List<JaversProperty> getProperties();

    Object getLeftPropertyValue(Property property);

    Object getRightPropertyValue(Property property);

    GlobalId getRightGlobalId(Property property);

    GlobalId getLeftGlobalId(Property property);

    ManagedType getManagedType();

    default Object getLeftPropertyValueAndSanitize(Property property, JaversType expectedType) {
        return sanitize(getLeftPropertyValue(property), expectedType);
    }

    default Object getRightPropertyValueAndSanitize(Property property, JaversType expectedType) {
        return sanitize(getRightPropertyValue(property), expectedType);
    }

    default Object sanitize(Object value, JaversType expectedType) {
        if (value == null) {
            return null;
        }

        if (expectedType.isInstance(value)) {
            return value;
        }

        if (expectedType instanceof EnumerableType) {
            return ((EnumerableType)expectedType).empty();
        }

        return null;
    }
}
