package org.javers.core.json.builder

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.model.domain.InstanceId
import org.javers.model.domain.ValueObjectId
import org.javers.model.mapping.Entity
import org.javers.model.mapping.Property
import org.javers.model.mapping.ValueObject

import static org.javers.core.json.builder.EntityTestBuilder.entity
import static org.javers.core.json.builder.EntityTestBuilder.valueObject
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

    static ValueChange valueChange(Object cdo, String propertyName, Object oldVal, Object newVal) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)


        new ValueChange(globalId, prop, oldVal, newVal)
    }

    static ValueChange valueObjectPropertyChange(Object instanceCdo,
                                                 Class valueObjectClass,
                                                 String voPropertyName, String instancePropertyName,
                                                 Object oldVal, Object newVal) {

        ValueObject valueObjectType = valueObject(valueObjectClass)

        Property prop = valueObjectType.getProperty(voPropertyName)
        ValueObjectId globalId = valueObjectId(instanceCdo, instancePropertyName)

        new ValueChange(globalId, prop, oldVal, newVal)
    }

    static ReferenceChange referenceChanged(Object cdo, String propertyName, Object oldRef , Object newRef) {
        InstanceId globalId = instanceId(cdo)
        Property prop = globalId.cdoClass.getProperty(propertyName)

        InstanceId oldRefId = instanceId(oldRef)
        InstanceId newRefId = instanceId(newRef)

        new ReferenceChange(globalId,prop, oldRefId, newRefId)
    }
}
