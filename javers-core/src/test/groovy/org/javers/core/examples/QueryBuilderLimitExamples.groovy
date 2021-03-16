package org.javers.core.examples

import org.javers.core.Changes
import org.javers.core.JaversBuilder
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
import spock.lang.Specification

import java.util.stream.Stream

import static org.javers.core.examples.model.Position.Specialist

class QueryBuilderLimitExamples extends Specification {

    def "Snapshots limit in findChanges and findShadows"() {
        given:
        def javers = JaversBuilder.javers().build()

        def bob = new Employee("Bob", 9_000, "ScrumMaster")
        bob.age = 20

        10.times {
            bob.salary += 1_000
            bob.age += 1
            javers.commit("author", bob)
        }

        def query = QueryBuilder.byInstanceId("Bob", Employee).limit(2).build()

        when: "findSnapshots - 2 latest snapshots are loaded and returned"
        List<CdoSnapshot> snapshots = javers.findSnapshots(query)
        snapshots.each {println(it)}

        then:
        snapshots.size() == 2

        when: "findChanges - two latest snapshots are loaded, 4 changes are returned"
        Changes changes = javers.findChanges(query)
        println changes.prettyPrint()

        then:
        changes.size() == 4
    }

    def "Shadows limit in findShadows and findShadowsAndStream"() {
        given:
        def javers = JaversBuilder.javers().build()

        def bob = new Employee("Bob", 9_000, "ScrumMaster")
        bob.primaryAddress = new Address("London")
        bob.postalAddress = new Address("Paris")

        3.times {
            bob.salary += 1_000
            bob.primaryAddress.city = "London $it"
            bob.postalAddress.city = "Paris $it"
            javers.commit("author", bob)
        }

        def query = QueryBuilder.byInstanceId("Bob", Employee).limit(2).build()

        when : "findShadows() - 9 snapshots are loaded, 2 Shadows are returned"
        List<Employee> shadows = javers.findShadows(query)
        shadows.each {println(it)}

        then:
        shadows.size() == 2
        println("query stats: " + query)

        when : "findShadowsAndStream() - 9 snapshots are loaded, 2 Shadows are returned"
        Stream<Shadow<Employee>> shadowsStream = javers.findShadowsAndStream(query)

        then:
        shadowsStream.count() == 2
    }
}
