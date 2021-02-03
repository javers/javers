package org.javers.core.graph

import org.javers.core.MappingStyle
import org.javers.core.metamodel.type.TypeFactoryTest

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class ObjectNodeFieldTest extends ObjectNodeTest{

    def setup() {
        createEntity = TypeFactoryTest.entityCreator(MappingStyle.FIELD)
    }
}

