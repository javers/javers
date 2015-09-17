package org.javers.core.metamodel.object

import org.javers.core.MappingStyle
import org.javers.core.metamodel.clazz.ManagedClassFactory
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
        managedClassFactory = javersTestAssembly(MappingStyle.FIELD).managedClassFactory
    }

    @Shared
    def ManagedClassFactory managedClassFactory

    def "should build value() from class name and id.toString for primitive Id "() {
        given:
        def entity = managedClassFactory.createEntity(Category)

        when:
        def instanceId = InstanceId.createFromId(12, entity)

        then:
        instanceId.cdoClass == entity
        instanceId.cdoId == 12
        instanceId.value() == Category.name + "/12"
    }

    def "should build value() from class name and id.toString for Embedded Id "() {
        given:
        def entity = managedClassFactory.createEntity(DummyEntityWithEmbeddedId)

        when:
        def instanceId = InstanceId.createFromId(new DummyPoint(1,3), entity)

        then:
        instanceId.cdoClass == entity
        instanceId.cdoId.x == 1
        instanceId.cdoId.y == 3
        instanceId.value() == DummyEntityWithEmbeddedId.name + "/1,3"
    }

}
