package org.javers.core.json.builder

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ValueChange
import org.javers.model.domain.GlobalCdoId
import org.javers.model.mapping.Property

/**
 * @author bartosz walacik
 */
class ChangeTestBuilder {

    static NewObject newObject(Object newObject) {
        GlobalCdoId globalId = GlobalCdoIdTestBuilder.globalCdoId(newObject)

        new NewObject(globalId, newObject)
    }

    static ObjectRemoved objectRemoved(Object objectRemoved) {
        GlobalCdoId globalId = GlobalCdoIdTestBuilder.globalCdoId(objectRemoved)

        new ObjectRemoved(globalId, objectRemoved)
    }

    static ValueChange valueChange(Object cdo, String propertyName, Object oldVal, Object newVal) {
        GlobalCdoId globalId = GlobalCdoIdTestBuilder.globalCdoId(cdo)
        Property prop = globalId.getEntity().getProperty(propertyName)


        new ValueChange(globalId, prop, oldVal, newVal)
    }
}
