package org.javers.core.snapshot;

import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.Collection;
import java.util.Collections;

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
    public Object getDehydratedPropertyValue(String property) {
        return getCdo().getPropertyValue(property);
    }

    @Override
    public Collection<GlobalId> getReferences(Property property) {

        Object propertyValue = getPropertyValue(property);
        if (propertyValue == null || !(propertyValue instanceof Collection)) {
            return Collections.emptyList();
        }

        Collection collection = (Collection) propertyValue;
        if (collection.size() == 0) {
            return Collections.emptyList();
        }

        Object firstItem = collection.iterator().next();
        if (firstItem instanceof GlobalId) {
            return collection;
        } else {
            //when user's class is refactored, a collection can contain different items
            return Collections.emptyList();
        }
    }
}
