package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

public interface NodePair {
    boolean isNullOnBothSides(Property property);

    GlobalCdoId getGlobalCdoId();

    ObjectNode getRight();

    List<Property> getProperties();

    Object getLeftPropertyValue(Property property);

    Object getRightPropertyValue(Property property);

    GlobalCdoId getRightGlobalCdoId(Property property);

    GlobalCdoId getLeftGlobalCdoId(Property property);
}
