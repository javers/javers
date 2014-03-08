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


abstract class ObjectWrapperTest extends Specification {

    protected ManagedClassFactory managedClassFactory

    def "should hold Entity reference"() {
        given:
        DummyUser cdo = dummyUser().build()
        Entity entity = managedClassFactory.createEntity(DummyUser)

        when:
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity)

        then:
        wrapper.managedClass == entity
    }

    
    def "should hold GlobalCdoId"() {
        given:
        DummyUser cdo = dummyUser().withName("Mad Kaz").build()
        Entity entity = managedClassFactory.createEntity(DummyUser)

        when:
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity)

        then:
        wrapper.globalCdoId == new InstanceId(cdo, entity)
    }
    
    def "should hold Cdo reference"() {
        given:
        DummyUser cdo = dummyUser().build()
        Entity entity = managedClassFactory.createEntity(DummyUser)

        when:
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity)

        then:
        wrapper.unwrapCdo() == cdo
    }

    
    def "should throw exceptin when Entity without id"() {
        given:
        DummyUser cdo = new DummyUser()
        Entity entity = managedClassFactory.createEntity(DummyUser)

        when:
        new ObjectWrapper(cdo, entity)

        then:
        JaversException exception = thrown(JaversException)
        exception.code == JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID
    }

    
    def "should be equal by id value and Entity class"() {
        when:
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))

        then:
        first.hashCode() == second.hashCode()
        first == second
    }

    
    def "should not be equal with different id value"() {
        when:
        ObjectWrapper first = new ObjectWrapper(new DummyUser("stach"), managedClassFactory.createEntity(DummyUser))
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax 1"), managedClassFactory.createEntity(DummyUser))

        then:
        first != second
    }

    
    def "should have reflexive equals method"() {
        when:
        ObjectWrapper objectWrapper = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))

        then:
        objectWrapper == objectWrapper
    }

    
    def "should have symmetric and transitive equals method"() {
        when:
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))
        ObjectWrapper third = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))

        then:
        first == second
        second == third
        first == third
    }

    
    def "should return false when equals method has null arg"() {
        when:
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.createEntity(DummyUser))

        then:
        first != null
    }

    def "should delegate equals and hash code to Cdo"() {
        when:
        Cdo mockedCdo = Mock()
        ObjectWrapper node1 = new ObjectWrapper(mockedCdo)
        ObjectWrapper node2 = new ObjectWrapper(mockedCdo)

        then:
        node1.hashCode() == mockedCdo.hashCode()
        node1 == node2
    }
}
