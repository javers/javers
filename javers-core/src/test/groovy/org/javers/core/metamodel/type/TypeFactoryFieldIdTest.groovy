package org.javers.core.metamodel.type

import org.javers.core.MappingStyle

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class TypeFactoryFieldIdTest extends TypeFactoryIdTest {

    def setup() {
        typeFactory = javersTestAssembly(MappingStyle.FIELD).typeSpawningFactory
    }
}
