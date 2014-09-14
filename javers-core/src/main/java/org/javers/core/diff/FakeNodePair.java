package org.javers.core.diff;

import org.javers.common.collections.Defaults;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

public class FakeNodePair implements NodePair {

    private final ObjectNode right;

    public FakeNodePair(ObjectNode right) {
        this.right = right;
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
    public List<Property> getProperties() {
        return right.getManagedClass().getProperties();
    }

    @Override
    public Object getLeftPropertyValue(Property property) {
        return Defaults.defaultValue(property.getType());
    }

    @Override
    public Object getRightPropertyValue(Property property) {
        return right.getPropertyValue(property);
    }

    @Override
    public GlobalId getRightGlobalId(Property property) {
         return right.getReference(property);
    }

    @Override
    public GlobalId getLeftGlobalId(Property property) {
        return null;
    }


}