package org.javers.shadow

import org.javers.core.JaversTestBuilder
import org.javers.core.MappingStyle
import org.javers.repository.jql.QueryBuilder

class FieldBasedShadowFactoryTest extends ShadowFactoryTest {
    @Override
    def setupSpec() {
        javersTestAssembly = JaversTestBuilder.javersTestAssembly(MappingStyle.FIELD)
        shadowFactory = javersTestAssembly.shadowFactory
        javers = javersTestAssembly.javers()
    }

    def "should manage immutable objects creation"(){
        given:
        def ref = new ImmutableEntity(2, null)
        def cdo = new ImmutableEntity(1, ref)
        javers.commit("author", cdo)
        def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, ImmutableEntity).build())[0]

        when:
        def shadow = shadowFactory.createShadow(snapshot, {s, id -> javers.findSnapshots(QueryBuilder.byInstanceId(id.cdoId, ImmutableEntity).build())[0]})

        then:
        shadow instanceof ImmutableEntity
        shadow.id == 1
        shadow.entityRef instanceof ImmutableEntity
        shadow.entityRef.id == 2
    }
}
