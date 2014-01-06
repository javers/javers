package org.javers.core.json.builder

import org.javers.core.JaversTestBuilder
import org.javers.model.mapping.Entity
import org.javers.model.mapping.EntityFactory
import org.javers.model.mapping.FieldBasedPropertyScanner

/**
 * @author bartosz walacik
 */
class EntityTestBuilder {
    EntityFactory entityFactory;

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
}
