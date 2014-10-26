package org.javers.core.json.builder

import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.clazz.Entity
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.clazz.ManagedClassFactory
import org.javers.core.metamodel.clazz.ValueObject
import org.javers.core.metamodel.clazz.ValueObjectDefinition

/**
 * @author bartosz walacik
 */
class EntityTestBuilder {
    ManagedClassFactory managedClassFactory;

    private EntityTestBuilder() {
        managedClassFactory = JaversTestBuilder.javersTestAssembly().managedClassFactory
    }

    static Entity entity(Class forClass, String idPropertyName) {
        EntityTestBuilder entityTestBuilder = new EntityTestBuilder()
        entityTestBuilder.managedClassFactory.create(new EntityDefinition(forClass, idPropertyName))
    }

    static Entity entity(Class forClass) {
        EntityTestBuilder entityTestBuilder = new EntityTestBuilder()
        entityTestBuilder.managedClassFactory.createEntity(forClass)
    }

    static ValueObject valueObject(Class forClass) {
        EntityTestBuilder entityTestBuilder = new EntityTestBuilder()
        entityTestBuilder.managedClassFactory.create(new ValueObjectDefinition(forClass))
    }
}
