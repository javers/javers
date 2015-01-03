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
    static ManagedClassFactory managedClassFactory;

    static iniManagedClassFactorySingleton() {
        if (managedClassFactory == null){
            managedClassFactory = JaversTestBuilder.javersTestAssembly().managedClassFactory
        }
    }

    static Entity entity(Class forClass, String idPropertyName) {
        iniManagedClassFactorySingleton();
        managedClassFactory.create(new EntityDefinition(forClass, idPropertyName))
    }

    static Entity entity(Class forClass) {
        iniManagedClassFactorySingleton();
        managedClassFactory.createEntity(forClass)
    }

    static ValueObject valueObject(Class forClass) {
        iniManagedClassFactorySingleton();
        managedClassFactory.create(new ValueObjectDefinition(forClass))
    }
}
