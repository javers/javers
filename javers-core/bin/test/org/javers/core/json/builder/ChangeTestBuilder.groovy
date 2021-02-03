package org.javers.core.json.builder

import org.javers.core.JaversTestBuilder
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.PropertyChangeMetadata
import org.javers.core.diff.changetype.PropertyChangeType
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.map.EntryChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.metamodel.object.GlobalIdFactory
import org.javers.core.metamodel.object.InstanceId

/**
 * @author bartosz walacik
 */
class ChangeTestBuilder {
    static GlobalIdFactory globalIdFactory = JaversTestBuilder.javersTestAssembly().globalIdFactory

    static NewObject newObject(Object newObject) {
        InstanceId globalId = instanceId(newObject)

        new NewObject(globalId, Optional.of(newObject))
    }

    static ObjectRemoved objectRemoved(Object objectRemoved) {
        InstanceId globalId = instanceId(objectRemoved)

        new ObjectRemoved(globalId, Optional.of(objectRemoved))
    }

    static setChange(Object cdo, String propertyName, List changes) {
        new SetChange(createMetadata(cdo, propertyName), changes)
    }
    static MapChange mapChange(Object cdo, String propertyName, List<EntryChange> changes) {
        new MapChange(createMetadata(cdo, propertyName), changes)
    }

    static ValueChange valueChange(Object cdo, String propertyName, oldVal=null, newVal=null) {
        new ValueChange(createMetadata(cdo, propertyName), oldVal, newVal)
    }

    static ReferenceChange referenceChanged(Object cdo, String propertyName, Object oldRef , Object newRef) {
        InstanceId oldRefId = instanceId(oldRef)
        InstanceId newRefId = instanceId(newRef)
        new ReferenceChange(createMetadata(cdo, propertyName), oldRefId, newRefId, null, null)
    }

    private static InstanceId instanceId(Object cdo) {
        if (cdo == null) {
            return null
        }

        globalIdFactory.createIdFromInstance(cdo)
    }

    static createMetadata(Object cdo, String propertyName) {
        InstanceId globalId = instanceId(cdo)
        new PropertyChangeMetadata(globalId, propertyName, Optional.empty(), PropertyChangeType.PROPERTY_VALUE_CHANGED)
    }
}
