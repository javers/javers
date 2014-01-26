package org.javers.core.json.builder

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.map.EntryChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.model.domain.GlobalCdoId
import org.javers.model.domain.InstanceId
import org.javers.model.domain.UnboundedValueObjectId
import org.javers.model.domain.ValueObjectId
import org.javers.core.metamodel.property.Property

import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.*

/**
 * @author bartosz walacik
 */
class ChangeTestBuilder {

    static NewObject newObject(Object newObject) {
        InstanceId globalId = instanceId(newObject)

        new NewObject(globalId, newObject)
    }

    static ObjectRemoved objectRemoved(Object objectRemoved) {
        InstanceId globalId = instanceId(objectRemoved)

        new ObjectRemoved(globalId, objectRemoved)
    }

    static MapChange mapChange(Object cdo, String propertyName, List<EntryChange> changes) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new MapChange(globalId, prop, changes)
    }

    static ValueChange valueChange(Object cdo, String propertyName, oldVal=null, newVal=null) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new ValueChange(globalId, prop, oldVal, newVal)
    }

    static ValueChange valueObjectPropertyChange(GlobalCdoId voOwnerId,
                                                 Class valueObjectCdoClass,
                                                 String voPropertyName, String fragment,
                                                 Object oldVal, Object newVal) {
        ValueObjectId valueObjectId = valueObjectId(voOwnerId,valueObjectCdoClass, fragment)
        new ValueChange(valueObjectId, valueObjectId.cdoClass.getProperty(voPropertyName), oldVal, newVal)
    }

    static ValueChange unboundedValueObjectPropertyChange( Class valueObjectCdoClass,
                                                           String voPropertyName,
                                                           Object oldVal, Object newVal) {

        UnboundedValueObjectId valueObjectId = unboundedValueObjectId(valueObjectCdoClass)
        new ValueChange(valueObjectId, valueObjectId.cdoClass.getProperty(voPropertyName), oldVal, newVal)
    }

    static ReferenceChange referenceChanged(Object cdo, String propertyName, Object oldRef , Object newRef) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)

        InstanceId oldRefId = instanceId(oldRef)
        InstanceId newRefId = instanceId(newRef)

        new ReferenceChange(globalId,prop, oldRefId, newRefId)
    }
}
