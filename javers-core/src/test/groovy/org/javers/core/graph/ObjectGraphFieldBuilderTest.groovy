package org.javers.core.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphFieldBuilderTest extends ObjectGraphBuilderTest {

    def setupSpec() {
        mapper = JaversTestBuilder.javersTestAssembly(MappingStyle.FIELD).typeMapper;
    }
}
