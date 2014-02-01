package org.javers.model.object.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphBeanBuilderTest extends ObjectGraphBuilderTest {

    def setup() {
        mapper = JaversTestBuilder.javersTestAssembly(MappingStyle.BEAN).typeMapper;
    }
}
