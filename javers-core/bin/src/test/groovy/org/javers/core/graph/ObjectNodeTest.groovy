package org.javers.core.graph

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.GlobalIdFactory
import org.javers.core.metamodel.type.EntityType
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.model.DummyUser.dummyUser

abstract class ObjectNodeTest extends Specification {

    protected def createEntity

    @Shared
    GlobalIdFactory globalIdFactory = JaversTestBuilder.javersTestAssembly().globalIdFactory

    private ObjectNode objectNode(Object cdo, EntityType entity) {
        new LiveNode(new LiveCdoWrapper(cdo, entity.createIdFromInstance(cdo), entity))
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

    def "should have reflexive equals method"() {
        when:
        def objectNode = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))

        then:
        objectNode == objectNode
    }

    def "should return false when equals method has null arg"() {
        when:
        ObjectNode first = objectNode(new DummyUser("Mad Kax"), createEntity(DummyUser))

        then:
        first != null
    }
}
