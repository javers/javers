package org.javers.core.graph

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.metamodel.object.Cdo
import org.javers.core.metamodel.type.EntityType
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.model.DummyUser.dummyUser

abstract class ObjectNodeTest extends Specification {

    protected def createEntity

    private ObjectNode objectNode(Object cdo, EntityType entity) {
        new ObjectNode<>(new LiveCdoWrapper(cdo, entity.createIdFromInstance(cdo), entity));
    }

    def "should hold Entity reference"() {
        given:
        def cdo = dummyUser()
        def entity = createEntity(DummyUser)

        when:
        def wrapper = objectNode(cdo, entity)

        then:
        wrapper.managedType == entity
    }


    def "should hold GlobalId"() {
        given:
        def cdo = dummyUser("Mad Kaz")
        def entity = createEntity(DummyUser)

        when:
        ObjectNode wrapper = objectNode(cdo, entity)

        then:
        wrapper.globalId == entity.createIdFromInstance(cdo)
    }

    def "should hold Cdo reference"() {
        given:
        def cdo = dummyUser()
        def entity = createEntity(DummyUser)

        when:
        def wrapper = objectNode(cdo, entity)

        then:
        wrapper.wrappedCdo().get() == cdo
    }


    def "should throw exception when Entity without id"() {
        given:
        def cdo = new DummyUser()
        def entity = createEntity(DummyUser)

        when:
        objectNode(cdo, entity)

        then:
        JaversException exception = thrown(JaversException)
        exception.code == JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID
    }


    def "should delegate equals() and hashCode() to CDO"() {
        when:
        def first = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))
        def second = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))

        then:
        first.hashCode() == second.hashCode()
        first == second
    }


    def "should not be equal when different CDO ids"() {
        when:
        def first = objectNode(new DummyUser("stach"), createEntity(DummyUser))
        def second = objectNode(new DummyUser("Mad Kaz 1"), createEntity(DummyUser))

        then:
        first != second
    }


    def "should have reflexive equals method"() {
        when:
        def objectNode = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))

        then:
        objectNode == objectNode
    }


    def "should have symmetric and transitive equals method"() {
        when:
        ObjectNode first = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))
        ObjectNode second = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))
        ObjectNode third = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))

        then:
        first == second
        second == third
        first == third
    }


    def "should return false when equals method has null arg"() {
        when:
        ObjectNode first = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))

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
