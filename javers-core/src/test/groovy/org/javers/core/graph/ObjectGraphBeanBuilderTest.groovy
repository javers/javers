package org.javers.core.graph

import org.javers.core.MappingStyle

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphBeanBuilderTest extends ObjectGraphBuilderTest {

    def setupSpec() {
        def javers = javersTestAssembly(MappingStyle.BEAN)
        mapper = javers.typeMapper
        liveCdoFactory = javers.liveCdoFactory
    }
}
