package org.javers.spring

import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.ValueObjectType
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification

/**
 * @author Pawel Cierpiatka
 */
class JaversSpringFactoryTest extends Specification {

    def "should registered entity and VO"() {
        given:
        def javersSpringFactory = new JaversSpringFactory()

        when:
        javersSpringFactory.entityClasses = [DummyUser, DummyUserDetails]
        javersSpringFactory.valueObjects = [DummyAddress, DummyNetworkAddress]

        then:
        javersSpringFactory.object.getForClass(DummyUser) instanceof EntityType
        javersSpringFactory.object.getForClass(DummyAddress) instanceof ValueObjectType
    }

    def "should registered described class with custom id"() {
        given:
        def javersSpringFactory = new JaversSpringFactory()

        when:
        javersSpringFactory.describedEntityClasses = [(DummyUser): "age"]

        then:
        def entityType = javersSpringFactory.object.getForClass(DummyUser)
        entityType.managedClass.idProperty.name == "age"
    }

}