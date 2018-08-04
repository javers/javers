package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;

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

    default <T> T getLeftPropertyValueAndCast(Property property, Class<T> expectedType) {
        return cast(getLeftPropertyValue(property), expectedType);
    }

    default <T> T getRightPropertyValueAndCast(Property property, Class<T> expectedType) {
        return cast(getRightPropertyValue(property), expectedType);
    }

    default <T> T cast(Object value, Class<T> expectedType) {
        if (value == null) {
            return null;
        }

        if (expectedType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (expectedType == Collection.class || expectedType == Set.class) {
            return (T)Collections.emptySet();
        }
        if (expectedType == List.class) {
            return (T)Collections.emptyList();
        }
        if (expectedType == Map.class) {
            return (T)Collections.emptyMap();
        }
        return null;
    }
}
