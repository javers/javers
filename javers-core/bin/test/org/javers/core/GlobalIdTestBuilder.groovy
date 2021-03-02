package org.javers.core

import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.object.ValueObjectId

class GlobalIdTestBuilder {
    static JaversTestBuilder javersTestBuilder = JaversTestBuilder.javersTestAssembly()

    static InstanceId instanceId(Object instance){
        javersTestBuilder.instanceId(instance)
    }

    static InstanceId instanceId(Object localId, Class entity){
        javersTestBuilder.instanceId(localId, entity)
    }

    static ValueObjectId valueObjectId(Object localId, Class owningEntity, fragment) {
        new ValueObjectId("?", instanceId(localId, owningEntity), fragment)
    }

    static UnboundedValueObjectId unboundedValueObjectId(Class valueObject) {
        javersTestBuilder.unboundedValueObjectId(valueObject)
    }
}
