package org.javers.core.metamodel.clazz

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.metamodel.property.EntityAssert
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyEntityWithEmbeddedId
import org.javers.core.model.DummyUser
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
abstract class EntityFactoryIdTest extends Specification {
    protected ManagedClassFactory entityFactory

    def "should use @EmbeddedId property"(){
        when:
        def entity = entityFactory.create(new EntityDefinition(DummyEntityWithEmbeddedId.class))

        then:
        EntityAssert.assertThat(entity).hasIdProperty("point")
    }

    def "should use @Id property by default"() {
        when:
        def entity = entityFactory.create(new EntityDefinition(DummyUser.class))

        then:
        EntityAssert.assertThat(entity).hasIdProperty("name")
    }

    def "should ignore @Id annotation when idProperty name is given"() {
        when:
        def entity = entityFactory.create(new EntityDefinition(DummyUser,"bigFlag"))

        then:
        EntityAssert.assertThat(entity).hasIdProperty("bigFlag")
    }

    def "should ignore @Transient annotation when idProperty name is given"() {
        when:
        def entity = entityFactory.create(new EntityDefinition(DummyUser,"propertyWithTransientAnn"))

        then:
        EntityAssert.assertThat(entity).hasIdProperty("propertyWithTransientAnn")
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
