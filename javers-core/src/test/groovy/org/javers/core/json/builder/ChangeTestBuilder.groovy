package org.javers.core.json.builder

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.model.domain.GlobalCdoId

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
}
