package org.javers.core.diff;

import com.google.common.collect.Streams;
import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

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
}