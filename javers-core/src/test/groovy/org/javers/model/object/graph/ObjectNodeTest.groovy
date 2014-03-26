package org.javers.model.object.graph

import org.javers.common.exception.exceptions.JaversException
import org.javers.common.exception.exceptions.JaversExceptionCode
import org.javers.core.metamodel.property.Entity
import org.javers.core.metamodel.property.ManagedClassFactory
import org.javers.core.model.DummyUser
import org.javers.core.metamodel.object.Cdo
import org.javers.core.metamodel.object.InstanceId
import spock.lang.Specification

import static org.javers.test.builder.DummyUserBuilder.dummyUser


abstract class ObjectNodeTest extends Specification {

    protected ManagedClassFactory managedClassFactory

    def "should hold Entity reference"() {
        given:
        DummyUser cdo = dummyUser().build()
        Entity entity = managedClassFactory.createEntity(DummyUser)

        when:
        ObjectNode wrapper = new ObjectNode(cdo, entity)

        then:
        wrapper.managedClass == entity
    }

    
    def "should hold GlobalCdoId"() {
        given:
        DummyUser cdo = dummyUser().withName("Mad Kaz").build()
        Entity entity = managedClassFactory.createEntity(DummyUser)

        when:
        ObjectNode wrapper = new ObjectNode(cdo, entity)

        then:
        wrapper.globalCdoId == new InstanceId(cdo, entity)
    }
    
    def "should hold Cdo reference"() {
        given:
        DummyUser cdo = dummyUser().build()
        Entity entity = managedClassFactory.createEntity(DummyUser)

        when:
        ObjectNode wrapper = new ObjectNode(cdo, entity)

        then:
        wrapper.wrappedCdo() == cdo
    }

    
    def "should throw exception when Entity without id"() {
        given:
        DummyUser cdo = new DummyUser()
        Entity entity = managedClassFactory.createEntity(DummyUser)

        when:
        new ObjectNode(cdo, entity)

        then:
        JaversException exception = thrown(JaversException)
        exception.code == JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID
    }

    
    def "should delegate equals() and hashCode() to cdo"() {
        when:
        ObjectNode first = new ObjectNode(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))
        ObjectNode second = new ObjectNode(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))

        then:
        first.hashCode() == second.hashCode()
        first == second
    }

    
    def "should not be equal when different cdo ids"() {
        when:
        ObjectNode first = new ObjectNode(new DummyUser("stach"), managedClassFactory.createEntity(DummyUser))
        ObjectNode second = new ObjectNode(new DummyUser("Mad Kaz 1"), managedClassFactory.createEntity(DummyUser))

        then:
        first != second
    }

    
    def "should have reflexive equals method"() {
        when:
        ObjectNode ObjectNode = new ObjectNode(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))

        then:
        ObjectNode == ObjectNode
    }

    
    def "should have symmetric and transitive equals method"() {
        when:
        ObjectNode first = new ObjectNode(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))
        ObjectNode second = new ObjectNode(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))
        ObjectNode third = new ObjectNode(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))

        then:
        first == second
        second == third
        first == third
    }

    
    def "should return false when equals method has null arg"() {
        when:
        ObjectNode first = new ObjectNode(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))

        then:
        first != null
    }

    def "should delegate equals and hash code to Cdo"() {
        when:
        Cdo mockedCdo = Mock()
        ObjectNode node1 = new ObjectNode(mockedCdo)
        ObjectNode node2 = new ObjectNode(mockedCdo)

        then:
        node1.hashCode() == mockedCdo.hashCode()
        node1 == node2
    }
}
