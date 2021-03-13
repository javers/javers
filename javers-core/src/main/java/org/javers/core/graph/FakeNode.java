package org.javers.core.graph;

import org.javers.common.collections.Defaults;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.Collections;
import java.util.List;

public class FakeNode extends ObjectNode<Cdo>{

    public FakeNode(Cdo cdo) {
        super(cdo);
    }

    @Override
    public boolean isEdge() {
        return true;
    }

    @Override
    public GlobalId getReference(Property property) {
        return null;
    }

    @Override
    public List<GlobalId> getReferences(JaversProperty property) {
        return Collections.emptyList();
    }

    @Override
    protected Object getDehydratedPropertyValue(String propertyName) {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED, "FakeLeftNodePair.getLeft()");
    }

    @Override
    public Object getPropertyValue(Property property) {
        return Defaults.defaultValue(property.getGenericType());
    }

    @Override
    public Object getDehydratedPropertyValue(JaversProperty property) {
        return Defaults.defaultValue(property.getGenericType());
    }

    @Override
    public boolean isNull(Property property) {
        return true;
    }
}
