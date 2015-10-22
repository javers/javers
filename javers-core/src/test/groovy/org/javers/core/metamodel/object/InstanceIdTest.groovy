package org.javers.core.metamodel.object

import org.javers.core.MappingStyle
import org.javers.core.metamodel.type.TypeFactory
import org.javers.core.model.Category
import org.javers.core.model.DummyEntityWithEmbeddedId
import org.javers.core.model.DummyPoint
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz.walacik
 */
class InstanceIdTest extends Specification {

    def setupSpec() {
        typeFactory = javersTestAssembly(MappingStyle.FIELD).typeSpawningFactory
    }

    @Shared
    def TypeFactory typeFactory

    def "should build value() from class name and id.toString for primitive Id "() {
        given:
        def entity = typeFactory.createEntity(Category)

        when:
        def instanceId = InstanceId.createFromId(12, entity)

        then:
        instanceId.cdoClass == entity
        instanceId.cdoId == 12
        instanceId.value() == Category.name + "/12"
    }

    def "should build value() from class name and id.toString for Embedded Id "() {
        given:
        def entity = typeFactory.createEntity(DummyEntityWithEmbeddedId)

        when:
        def instanceId = InstanceId.createFromId(new DummyPoint(1,3), entity)

        then:
        instanceId.cdoClass == entity
        instanceId.cdoId.x == 1
        instanceId.cdoId.y == 3
        instanceId.value() == DummyEntityWithEmbeddedId.name + "/1,3"
    }

}
