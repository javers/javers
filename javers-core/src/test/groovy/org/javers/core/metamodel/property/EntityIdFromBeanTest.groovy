package org.javers.core.metamodel.property

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class EntityIdFromBeanTest extends EntityIdTest {

    def setup() {
        entityFactory = javersTestAssembly(MappingStyle.BEAN).managedClassFactory
    }
}
