package org.javers.core.graph

import org.javers.core.MappingStyle

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphFieldBuilderTest extends ObjectGraphBuilderTest {

    def setupSpec() {
        def javers = javersTestAssembly(MappingStyle.FIELD)
        mapper = javers.typeMapper
        liveCdoFactory = javers.liveCdoFactory
    }
}
