package org.javers.core.metamodel.clazz

import org.javers.core.MappingStyle

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class EntityFactoryBeanIdTest extends EntityFactoryIdTest {

    def setup() {
        entityFactory = javersTestAssembly(MappingStyle.BEAN).managedClassFactory
    }
}
