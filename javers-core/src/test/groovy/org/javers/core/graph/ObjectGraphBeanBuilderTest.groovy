package org.javers.core.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphBeanBuilderTest extends ObjectGraphBuilderTest {

    def setupSpec() {
        def javers = JaversTestBuilder.javersTestAssembly(MappingStyle.BEAN)
        mapper = javers.typeMapper
        liveCdoFactory = javers.liveCdoFactory
    }
}
