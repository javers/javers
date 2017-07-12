package org.javers.core.diff;

import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;

import java.util.List;

/**
 * holds two versions of the same {@link ObjectNode}
 *
 * @author bartosz walacik
 */
public class RealNodePair implements NodePair {
    private final ObjectNode left;
    private final ObjectNode right;

    public RealNodePair(ObjectNode left, ObjectNode right) {
        Validate.argumentsAreNotNull(left, right);
        Validate.argumentCheck(left.getGlobalId().equals(right.getGlobalId()), "left & right should refer to the same Cdo");
        this.left = left;
        this.right = right;
    }

    @Override
    public ManagedType getManagedType() {
        return left.getManagedType();
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
    public GlobalId getRightGlobalId(Property property) {
        return right.getReference(property);
    }

    @Override
    public GlobalId getLeftGlobalId(Property property) {
        return left.getReference(property);
    }

    @Override
    public ObjectNode getRight() {
        return right;
    }

    @Override
    public List<JaversProperty> getProperties() {
        return getManagedType().getProperties();
    }

    @Override
    public GlobalId getGlobalId() {
        return left.getGlobalId();
    }
}