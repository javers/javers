package org.javers.core.json.builder

import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.clazz.ValueObjectDefinition
import org.javers.core.metamodel.type.*

/**
 * @author bartosz walacik
 */
@Deprecated
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
