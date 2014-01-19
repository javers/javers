package org.javers.core.json.builder

import org.javers.core.diff.changetype.Entry
import org.javers.core.diff.changetype.EntryAdded
import org.javers.core.diff.changetype.EntryChanged
import org.javers.core.diff.changetype.EntryRemoved
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.model.domain.GlobalCdoId
import org.javers.model.domain.InstanceId
import org.javers.model.domain.UnboundedValueObjectId
import org.javers.model.domain.ValueObjectId
import org.javers.model.mapping.Property

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

    static EntryChanged entryChange(Object cdo, String propertyName, key="key", oldVal=null, newVal=null) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new EntryChanged(globalId,prop,key,oldVal,newVal)
    }

    static EntryAdded entryAdded(Object cdo, String propertyName, key="key", val=null) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new EntryAdded(globalId,prop, new Entry(key,val))
    }

    static EntryRemoved entryRemoved(Object cdo, String propertyName, key="key", val=null) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)
        new EntryRemoved(globalId,prop,new Entry(key,val))
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
