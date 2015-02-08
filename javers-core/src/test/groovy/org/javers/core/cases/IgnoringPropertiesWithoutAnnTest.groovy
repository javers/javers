package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.model.DummyUser
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/94
 *
 * Specify ignored properties without annotations
 *
 * @author bartosz walacik
 */
class IgnoringPropertiesWithoutAnnTest extends Specification {
    def "should ignore properties"() {
        given:
        def javers = JaversBuilder.javers().registerEntity(DummyUser, "name", ["surname"]).build()

        when:
        def user1 = new DummyUser("Lord", "Smith")
        def user2 = new DummyUser("Lord", "Garmadon")

        def diff = javers.compare(user1, user2)

        then:
        ! diff.changes
    }
}
