package org.javers.core.metamodel.clazz

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.metamodel.clazz.Entity
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.clazz.ManagedClassFactory
import org.javers.core.metamodel.property.EntityAssert
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
abstract class EntityFactoryIdTest extends Specification {
    protected ManagedClassFactory entityFactory

    def "should use @id property by default"() {
        when:
        def entity = entityFactory.create(new EntityDefinition(DummyUser.class))

        then:
        EntityAssert.assertThat(entity).hasIdProperty("name")
    }

    def "should ignore @Id annotation where idProperty name is given"() {
        when:
        def entity = entityFactory.create(new EntityDefinition(DummyUser,"bigFlag"))

        then:
        EntityAssert.assertThat(entity).hasIdProperty("bigFlag")
    }

    def "should fail for Entity without Id property"() {
        when:
        entityFactory.createEntity(DummyAddress.class)

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.ENTITY_WITHOUT_ID
    }

    def "should fail when given Id property name doesn't exists"() {
        when:
        entityFactory.create(new EntityDefinition(DummyUser,"zonk"))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.PROPERTY_NOT_FOUND
    }
}
