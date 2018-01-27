package org.javers.core.metamodel.type

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyEntityWithEmbeddedId
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
abstract class TypeFactoryIdTest extends Specification {
    @Shared
    TypeFactory typeFactory

    def "should use @EmbeddedId property"(){
        when:
        def entity = typeFactory.create(new EntityDefinition(DummyEntityWithEmbeddedId.class))

        then:
        entity.idProperty.name == 'point'
    }

    def "should use @Id property by default"() {
        when:
        def entity = typeFactory.create(new EntityDefinition(DummyUser.class))

        then:
        entity.idProperty.name == 'name'
    }

    def "should ignore @Id annotation when idProperty name is given"() {
        when:
        def entity = typeFactory.create(new EntityDefinition(DummyUser,"bigFlag"))

        then:
        entity.idProperty.name == 'bigFlag'
    }

    def "should not ignore @Transient annotation when idProperty name is given"() {
        when:
        typeFactory.create(new EntityDefinition(DummyUser,"propertyWithTransientAnn"))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.PROPERTY_NOT_FOUND
        println(e)
    }

    def "should fail for Entity without Id property"() {
        when:
        typeFactory.create(new EntityDefinition(DummyAddress.class))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.ENTITY_WITHOUT_ID
    }

    def "should fail when given Id property name doesn't exists"() {
        when:
        typeFactory.create(new EntityDefinition(DummyUser,"zonk"))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.PROPERTY_NOT_FOUND
    }
}
