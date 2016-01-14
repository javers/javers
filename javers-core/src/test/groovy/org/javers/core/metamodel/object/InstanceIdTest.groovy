package org.javers.core.metamodel.object

import org.javers.core.model.DummyPoint
import spock.lang.Specification


/**
 * @author bartosz.walacik
 */
class InstanceIdTest extends Specification {

    def "should build value() from typeName and id.toString for primitive Id "() {
        when:
        def instanceId = new InstanceId("entity", 12)

        then:
        instanceId.typeName == "entity"
        instanceId.cdoId == 12
        instanceId.value() == "entity/12"
    }

    def "should build value() from typeName and id.toString for Embedded Id "() {
        given:

        when:
        def instanceId = new InstanceId("entity", new DummyPoint(1,3))

        then:
        instanceId.typeName == "entity"
        instanceId.cdoId.x == 1
        instanceId.cdoId.y == 3
        instanceId.value() == "entity/1,3"
    }

}
