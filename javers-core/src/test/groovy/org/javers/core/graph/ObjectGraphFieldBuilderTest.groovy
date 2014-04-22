package org.javers.core.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphFieldBuilderTest extends ObjectGraphBuilderTest {

    def setupSpec() {
        def javers = JaversTestBuilder.javersTestAssembly(MappingStyle.FIELD)
        mapper = javers.typeMapper
        liveCdoFactory = javers.liveCdoFactory
    }
}
