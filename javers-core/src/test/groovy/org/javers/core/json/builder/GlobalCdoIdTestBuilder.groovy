package org.javers.core.json.builder

import org.javers.core.metamodel.object.GlobalCdoId
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.object.ValueObjectId

import static EntityTestBuilder.entity
import static EntityTestBuilder.valueObject

/**
 * @author bartosz walacik
 */
@Deprecated
class GlobalCdoIdTestBuilder {

    @Deprecated
    static InstanceId instanceId(Object id, Class clazz) {
        return InstanceId.createFromId(id, entity(clazz))
    }

    @Deprecated
    static InstanceId instanceId(Object cdo) {
        if (cdo == null) {
            return null
        }

        return InstanceId.createFromInstance(cdo, entity(cdo.getClass()))
    }

    @Deprecated
    static UnboundedValueObjectId unboundedValueObjectId(Object cdoClass) {
        new UnboundedValueObjectId(valueObject(cdoClass))
    }

    @Deprecated
    static ValueObjectId valueObjectId(GlobalCdoId ownerId, Class valueObjectCdoClass, String fragment) {
        new ValueObjectId(valueObject(valueObjectCdoClass), ownerId, fragment)
    }

}
