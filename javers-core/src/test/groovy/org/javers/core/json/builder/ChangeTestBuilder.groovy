package org.javers.core.json.builder

import org.javers.common.collections.Optional
import org.javers.core.JaversTestBuilder
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ArrayChange
import org.javers.core.diff.changetype.container.ContainerElementChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.map.EntryChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.property.Property
import org.javers.core.metamodel.type.TypeMapper

/**
 * @author bartosz walacik
 */
class ChangeTestBuilder {
    static TypeMapper typeMapper = JaversTestBuilder.javersTestAssembly().typeMapper

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
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new MapChange(globalId, prop, changes)
    }

    static ListChange listChange(Object cdo, String propertyName, List<ContainerElementChange> changes) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new ListChange(globalId, prop, changes)
    }

    static ArrayChange arrayChange(Object cdo, String propertyName, List<ContainerElementChange> changes) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new ArrayChange(globalId, prop, changes)
    }

    static SetChange setChange(Object cdo, String propertyName, List<ContainerElementChange> changes) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new SetChange(globalId, prop, changes)
    }

    static ValueChange valueChange(Object cdo, String propertyName, oldVal=null, newVal=null) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new ValueChange(globalId, prop, oldVal, newVal)
    }

    static ReferenceChange referenceChanged(Object cdo, String propertyName, Object oldRef , Object newRef) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)

        InstanceId oldRefId = instanceId(oldRef)
        InstanceId newRefId = instanceId(newRef)

        new ReferenceChange(globalId,prop, oldRefId, newRefId)
    }

    private static InstanceId instanceId(Object cdo) {
        if (cdo == null) {
            return null
        }

        return InstanceId.createFromInstance(cdo, typeMapper.getJaversType(cdo.getClass()))
    }
}
