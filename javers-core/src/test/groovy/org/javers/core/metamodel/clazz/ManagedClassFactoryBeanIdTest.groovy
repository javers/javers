package org.javers.core.metamodel.clazz

import org.javers.core.MappingStyle

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class ManagedClassFactoryBeanIdTest extends ManagedClassFactoryIdTest {

    def setup() {
        managedClassFactory = javersTestAssembly(MappingStyle.BEAN).managedClassFactory
    }
}
