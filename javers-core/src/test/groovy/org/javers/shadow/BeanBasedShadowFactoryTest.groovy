package org.javers.shadow

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle
import org.javers.core.metamodel.object.CdoSnapshot

class BeanBasedShadowFactoryTest extends ShadowFactoryTest {
    @Override
    def setupSpec() {
        javersTestAssembly = JaversTestBuilder.javersTestAssembly(MappingStyle.BEAN)
        shadowFactory = javersTestAssembly.shadowFactory
        javers = javersTestAssembly.javers()
    }

    def "should throw SETTER_NOT_FOUND Exception when setter is not found"() {
        given:
        def entity = new ImmutableEntity(1, new ImmutableEntity(2))
        javers.commit("author", entity)

        when:
        CdoSnapshot snapshot = javers.getLatestSnapshot(1, ImmutableEntity).get()
        shadowFactory.createShadow(snapshot, byIdSupplier())

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.SETTER_NOT_FOUND
    }
}
