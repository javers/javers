package org.javers.spring

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification

/**
 * @author Pawel Cierpiatka
 */
class JaversSpringFactoryTest extends Specification {

    def "should registered entity "() {
        given:
        JaversSpringFactory javersSpringFactory = new JaversSpringFactory()

        when:
        javersSpringFactory.entityClasses = [DummyUser, DummyUserDetails]
        javersSpringFactory.valueObject = [DummyAddress, DummyNetworkAddress]

        then:
        javersSpringFactory.object.isManaged(DummyUser.class)
    }

    def "should registered described class with custom id"() {
        given:
        JaversSpringFactory javersSpringFactory = new JaversSpringFactory()

        when:
        javersSpringFactory.describedEntityClasses = [(DummyUser) : "age"]
        javersSpringFactory.entityClasses = [DummyUserDetails]
        javersSpringFactory.valueObject = [DummyAddress, DummyNetworkAddress]

        then:
        javersSpringFactory.object.isManaged(DummyUser.class)
    }

}