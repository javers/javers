package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.clazz.EntityDefinitionBuilder
import org.javers.core.metamodel.clazz.ValueObjectDefinition
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.metamodel.clazz.EntityDefinitionBuilder.entityDefinition

/**
 * https://github.com/javers/javers/issues/94
 *
 * Specify ignored properties without annotations
 *
 * @author bartosz walacik
 */
class IgnoringPropertiesWithoutAnnTest extends Specification {
    def "should ignore selected properties of Entity"() {
        given:
        def javers = JaversBuilder.javers().registerEntity( new EntityDefinition(DummyUser, "name", ["surname"])).build()

        when:
        def user1 = new DummyUser("Lord", "Smith")
        def user2 = new DummyUser("Lord", "Garmadon")

        def diff = javers.compare(user1, user2)

        then:
        ! diff.changes
    }

    def "should ignore selected properties of ValueObject"() {
        given:
        def javers = JaversBuilder.javers().registerValueObject( new ValueObjectDefinition(DummyUser, ["surname"])).build()

        when:
        def user1 = new DummyUser("Lord", "Smith")
        def user2 = new DummyUser("Lord", "Garmadon")

        def diff = javers.compare(user1, user2)

        then:
        ! diff.changes
    }
}
