package org.javers.core.metamodel.object

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
}
