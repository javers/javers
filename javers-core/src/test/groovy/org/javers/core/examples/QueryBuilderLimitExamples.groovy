package org.javers.core.examples

import org.javers.core.JaversBuilder
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.repository.jql.QueryBuilder
import spock.lang.Ignore
import spock.lang.Specification
import java.time.ZonedDateTime
import static org.javers.core.examples.model.Position.Specialist

class QueryBuilderLimitExamples extends Specification {

    @Ignore
    def "snapshot limit in findChanges and findShadows"() {
        given:
        def javers = JaversBuilder.javers().build()

        def bob = new Employee("Bob", 9_000, "ScrumMaster")
        javers.commit("author", bob)

        bob.salary += 1_000
        bob.position = Specialist
        bob.age = 21
        bob.lastPromotionDate = ZonedDateTime.now()
        javers.commit("author", bob)

        def query = QueryBuilder.byInstanceId("Bob", Employee).limit(2).build()

        when: "findChanges"
        def changes = javers.findChanges(query)
        println changes.prettyPrint()

        then:
        changes.size() == 8

        when: "findSnapshots"
        def snapshots = javers.findSnapshots(query)
        snapshots.each {println(it)}

        then:
        snapshots.size() == 2
    }

    def "shadows limit in findShadows and findShadowsAndStream"() {
        given:
        def javers = JaversBuilder.javers().build()

        def bob = new Employee("Bob", 9_000, "ScrumMaster")
        bob.primaryAddress = new Address("London")
        javers.commit("author", bob) // 2 snapshots are persisted

        bob.salary += 1_000
        bob.primaryAddress.city = "New York"
        javers.commit("author", bob) // 3 snapshots are persisted

        def query = QueryBuilder.byInstanceId("Bob", Employee).limit(2).build()

        when : "findShadows()"
        def shadows = javers.findShadows(query)
        shadows.each {println(it)}

        then:
        shadows.size() == 2
        println("query.streamStats().get(): " + query.streamStats().get())
        println("query: " + query)

        when : "findShadowsAndStream()"
        shadows = javers.findShadowsAndStream(query).toArray()
        shadows.each {println(it)}

        then:
        shadows.size() == 2
    }
}
