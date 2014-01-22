package org.javers.core.diff;

import org.javers.common.validation.Validate;
import org.javers.model.domain.Cdo;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.mapping.Property;
import org.javers.model.object.graph.Edge;
import org.javers.model.object.graph.ObjectNode;
import org.javers.model.object.graph.SingleEdge;

import java.util.List;

/**
 * holds two versions of the same {@link ObjectNode}
 *
 * @author bartosz walacik
 */
//TODO refactor -> extends FakeNodePair???
public class RealNodePair implements NodePair {
    private final ObjectNode left;
    private final ObjectNode right;

    public RealNodePair(ObjectNode left, ObjectNode right) {
        Validate.argumentsAreNotNull(left, right);
        Validate.argumentCheck(left.getGlobalCdoId().equals(right.getGlobalCdoId()),"left & right should refer to the same Cdo");
        this.left = left;
        this.right = right;
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
    public GlobalCdoId getRightGlobalCdoId(Property property) {
        //TODO refactor
        return getGlobalCdoId(right.getEdge(property));
    }

    @Override
    public GlobalCdoId getLeftGlobalCdoId(Property property) {
        //TODO refactor
        return getGlobalCdoId(left.getEdge(property));
    }

    public ObjectNode getLeft() {
        return left;
    }

    @Override
    public ObjectNode getRight() {
        return right;
    }

    @Override
    public List<Property> getProperties() {
        return left.getManagedClass().getProperties();
    }

    @Override
    public GlobalCdoId getGlobalCdoId() {
        return left.getGlobalCdoId();
    }

    //TODO refactor <-> FakeNodePair
    private GlobalCdoId getGlobalCdoId(Edge edge) {
        return edge != null ? ((SingleEdge) edge).getReference().getGlobalCdoId() : null;
    }
}
