package org.javers.core.snapshot;

import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;
import java.util.Collections;
import java.util.List;

class SnapshotNode extends ObjectNode<CdoSnapshot> {

    public SnapshotNode(CdoSnapshot cdo) {
        super(cdo);
    }

    @Override
    public GlobalId getReference(Property property){

        Object propertyValue = getPropertyValue(property);
        if (propertyValue instanceof GlobalId) {
            return (GlobalId)propertyValue;
        } else {
            //when user's class is refactored, a property can have different type
            return null;
        }
    }

    @Override
    protected Object getDehydratedPropertyValue(String property) {
        return getCdo().getPropertyValue(property);
    }

    @Override
    public Object getDehydratedPropertyValue(JaversProperty property) {
        return getCdo().getPropertyValue(property);
    }

    @Override
    public List<GlobalId> getReferences(JaversProperty property) {
        if (property.getType() instanceof EnumerableType) {
            Object propertyValue = getPropertyValue(property);
            EnumerableType enumerableType = property.getType();
            return enumerableType.filterToList(propertyValue, GlobalId.class);
        }
        else {
            return Collections.emptyList();
        }
    }
}
