package org.javers.core.diff;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;
import org.javers.model.object.graph.ObjectNode;

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
