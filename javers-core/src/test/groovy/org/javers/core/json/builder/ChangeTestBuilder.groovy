package org.javers.core.json.builder

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.model.domain.GlobalCdoId
import org.javers.model.mapping.Property

import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.globalCdoId

/**
 * @author bartosz walacik
 */
class ChangeTestBuilder {

    static NewObject newObject(Object newObject) {
        GlobalCdoId globalId = globalCdoId(newObject)

        new NewObject(globalId, newObject)
    }

    static ObjectRemoved objectRemoved(Object objectRemoved) {
        GlobalCdoId globalId = globalCdoId(objectRemoved)

        new ObjectRemoved(globalId, objectRemoved)
    }

    static ValueChange valueChange(Object cdo, String propertyName, Object oldVal, Object newVal) {
        GlobalCdoId globalId = globalCdoId(cdo)
        Property prop = globalId.getEntity().getProperty(propertyName)


        new ValueChange(globalId, prop, oldVal, newVal)
    }

    static ReferenceChange referenceChanged(Object cdo, String propertyName, Object oldRef , Object newRef) {
        GlobalCdoId globalId = globalCdoId(cdo)
        Property prop = globalId.getEntity().getProperty(propertyName)

        GlobalCdoId oldRefId = globalCdoId(oldRef)
        GlobalCdoId newRefId = globalCdoId(newRef)

        new ReferenceChange(globalId,prop, oldRefId, newRefId)
    }
}
