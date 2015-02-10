package org.javers.core.metamodel.clazz

import org.javers.core.MappingStyle

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class EntityFieldIdTest extends EntityFactoryIdTest {

    def setup() {
        entityFactory = javersTestAssembly(MappingStyle.FIELD).managedClassFactory
    }
}
