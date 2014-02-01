package org.javers.core.json.builder

import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.property.Entity
import org.javers.core.metamodel.property.ManagedClassFactory
import org.javers.core.metamodel.property.ValueObject
import org.javers.core.metamodel.property.ValueObjectDefinition

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
        entityTestBuilder.entityFactory.createEntity(forClass)
    }

    static ValueObject valueObject(Class forClass) {
        EntityTestBuilder entityTestBuilder = new EntityTestBuilder()
        entityTestBuilder.entityFactory.create(new ValueObjectDefinition(forClass))
    }
}
