package org.javers.core.diff;

import org.javers.model.domain.GlobalCdoId;
import org.javers.core.metamodel.property.Property;
import org.javers.model.object.graph.Edge;
import org.javers.model.object.graph.ObjectNode;
import org.javers.model.object.graph.SingleEdge;

import java.util.List;

public class FakeNodePair implements NodePair {

    private ObjectNode right;

    public FakeNodePair(ObjectNode right) {
        this.right = right;
    }

    @Override
    public boolean isNullOnBothSides(Property property) {
        return right.getPropertyValue(property) == null;
    }

    @Override
    public GlobalCdoId getGlobalCdoId() {
        return right.getGlobalCdoId();
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
        return null;
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
        return null;
    }

    //TODO refactor <-> RealNodePair
    private GlobalCdoId getGlobalCdoId(Edge edge) {
        return edge != null ? ((SingleEdge) edge).getReference().getGlobalCdoId() : null;
    }
}