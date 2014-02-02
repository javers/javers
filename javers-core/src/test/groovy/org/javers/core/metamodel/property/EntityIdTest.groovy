package org.javers.core.metamodel.property

import org.javers.core.exceptions.JaversException
import org.javers.core.exceptions.JaversExceptionCode
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
abstract class EntityIdTest extends Specification {
    protected ManagedClassFactory entityFactory

    def "should use @id property by default"() {
        when:
        Entity entity = entityFactory.create(new EntityDefinition(DummyUser.class))

        then:
        EntityAssert.assertThat(entity).hasIdProperty("name")
    }

    def "should use custom id property when given"() {
        when:
        Entity entity = entityFactory.create(new EntityDefinition(DummyUser.class,"bigFlag"))

        then:
        EntityAssert.assertThat(entity).hasIdProperty("bigFlag")
    }

    def "should throw exception when entity without id"() {
        when:
        entityFactory.createEntity(DummyAddress.class)

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.ENTITY_WITHOUT_ID
    }
}
