package org.javers.core.json.builder

import org.javers.model.domain.GlobalCdoId
import org.javers.model.domain.InstanceId
import org.javers.model.domain.UnboundedValueObjectId
import org.javers.model.domain.ValueObjectId
import org.javers.core.metamodel.property.Entity

import static EntityTestBuilder.entity
import static EntityTestBuilder.valueObject

/**
 * @author bartosz walacik
 */
class GlobalCdoIdTestBuilder {

    static InstanceId instanceId(Object cdo) {
        if (cdo == null) {
            return null
        }

        Entity entity = entity(cdo.class)

        new InstanceId(cdo, entity)
    }

    static UnboundedValueObjectId unboundedValueObjectId(Object cdoClass) {
        new UnboundedValueObjectId(valueObject(cdoClass))
    }

    static ValueObjectId valueObjectId(GlobalCdoId ownerId, Class valueObjectCdoClass, String fragment) {
        new ValueObjectId(valueObject(valueObjectCdoClass), ownerId, fragment)
    }

}
