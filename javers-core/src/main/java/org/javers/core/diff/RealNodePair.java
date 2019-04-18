package org.javers.core.diff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;

import java.util.Collection;
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
        if(left == null)
            return right.getManagedType();
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

    @Override
    public List<JaversProperty> getProperties() {
        List<JaversProperty> rightList = right.getManagedType().getProperties();
        List<JaversProperty> leftList = left.getManagedType().getProperties();
        final Set<JaversProperty> collect = new HashSet<>(rightList);
        final Set<JaversProperty> collect2 = new HashSet<>(leftList);
        collect.addAll(collect2);
        return new ArrayList<>(collect);

    }

    @Override
    public GlobalId getGlobalId() {
        if(left.getCdo() == null)
            return right.getGlobalId();
        return left.getGlobalId();
    }
}