package org.javers.shadow

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle

class BeanBasedShadowFactoryTest extends ShadowFactoryTest {
    @Override
    def setupSpec() {
        javersTestAssembly = JaversTestBuilder.javersTestAssembly(MappingStyle.BEAN)
        shadowFactory = javersTestAssembly.shadowFactory
        javers = javersTestAssembly.javers()
    }
}
