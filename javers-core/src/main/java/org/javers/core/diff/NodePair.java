package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;

import java.util.List;

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
}
