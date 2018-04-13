package org.javers.core.examples

import org.javers.core.Changes
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.examples.model.Person
import org.javers.core.examples.model.Position
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.repository.jql.JqlQuery
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
import spock.lang.Specification

class BasicCommitAndQueryExample extends Specification {

    def "should commit and query from JaversRepository"() {
        given:
        // prepare JaVers instance. By default, JaVers uses InMemoryRepository,
        // it's useful for testing
        Javers javers = JaversBuilder.javers().build()

        Person robert = new Person("bob", "Robert Martin")
        javers.commit("user", robert)           // persist initial commit

        robert.setName("Robert C.")             // do some changes
        robert.setPosition(Position.Developer)
        javers.commit("user", robert)           // and persist another commit

        JqlQuery query = QueryBuilder.byInstanceId("bob", Person.class).build()

        when:
        println "Shadows query:"

        List<Shadow<Person>> shadows = javers.findShadows(query)

        shadows.forEach { println it.get() }

        then: "there should be two Bob's Shadows"
        assert shadows.size == 2

        when:
        println "Snapshots query:"

        List<CdoSnapshot> snapshots = javers.findSnapshots(query)

        snapshots.forEach { println it }

        then: "there should be two Bob's Shadows"
        assert snapshots.size == 2

        when:
        println "Changes query:"

        Changes changes = javers.findChanges(query)
        // or the old approach:
        // List<Change> changes = javers.findChanges(query)

        println changes.prettyPrint()

        then: "there should be two Changes on Bob"
        assert changes.size() == 2
    }
}

