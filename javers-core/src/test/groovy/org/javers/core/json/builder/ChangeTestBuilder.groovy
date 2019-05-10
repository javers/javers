package org.javers.core.json.builder

import org.javers.core.JaversTestBuilder
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
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

    static MapChange mapChange(Object cdo, String propertyName, List<EntryChange> changes) {
        InstanceId globalId = instanceId(cdo)
        new MapChange(globalId, propertyName, changes)
    }

    static ValueChange valueChange(Object cdo, String propertyName, oldVal=null, newVal=null) {
        InstanceId globalId = instanceId(cdo)
        new ValueChange(globalId, propertyName, oldVal, newVal, Optional.empty())
    }

    static ReferenceChange referenceChanged(Object cdo, String propertyName, Object oldRef , Object newRef) {
        InstanceId globalId = instanceId(cdo)
        InstanceId oldRefId = instanceId(oldRef)
        InstanceId newRefId = instanceId(newRef)

        ReferenceChange.create(globalId, propertyName, oldRefId, newRefId, null, null)
    }

    static ReferenceChange referenceAdded(Object cdo, String propertyName, Object newRef) {
        new ReferenceChange.ReferenceAddedChange(instanceId(cdo), propertyName, instanceId(newRef), null, Optional.empty())
    }

    static ReferenceChange referenceRemoved(Object cdo, String propertyName, Object oldRef) {
        new ReferenceChange.ReferenceRemovedChange(instanceId(cdo), propertyName, instanceId(oldRef), null, Optional.empty())
    }

    private static InstanceId instanceId(Object cdo) {
        if (cdo == null) {
            return null
        }

        globalIdFactory.createIdFromInstance(cdo)
    }
}
