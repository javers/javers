package org.javers.core.json.builder

import org.javers.core.JaversTestBuilder
import org.javers.model.mapping.Entity
import org.javers.model.mapping.ManagedClassFactory
import org.javers.model.mapping.ValueObject
import org.javers.model.mapping.ValueObjectDefinition

/**
 * @author bartosz walacik
 */
class EntityTestBuilder {
    ManagedClassFactory entityFactory;

    private EntityTestBuilder() {
        entityFactory = JaversTestBuilder.javersTestAssembly().entityFactory
    }

    static Entity entity(Class forClass, String idPropertyName) {
        EntityTestBuilder entityTestBuilder = new EntityTestBuilder()
        entityTestBuilder.entityFactory.create(forClass, idPropertyName)
    }

    static Entity entity(Class forClass) {
        EntityTestBuilder entityTestBuilder = new EntityTestBuilder()
        entityTestBuilder.entityFactory.create(forClass)
    }

    static ValueObject valueObject(Class forClass) {
        EntityTestBuilder entityTestBuilder = new EntityTestBuilder()
        entityTestBuilder.entityFactory.create(new ValueObjectDefinition(forClass))
    }
}
