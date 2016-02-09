package org.javers.core.metamodel.type

import org.javers.core.MappingStyle

/**
 * @author bartosz walacik
 */
class TypeFactoryFieldIdTest extends TypeFactoryIdTest {

    def setupSpec() {
        typeFactory = TypeFactoryTest.create(MappingStyle.FIELD)
    }
}
