package org.javers.core.cases


import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Stream

/**
 * https://github.com/javers/javers/issues/822
 */
class ShadowStreamLimitBug extends Specification {

    @Unroll
    def "should find shadows and stream with limit = #limit and snapshotQueryLimit = #snapshotQueryLimit" () {
        given:
        Javers javers = JaversBuilder.javers().build()
        Employee frodo = new Employee("Frodo")
        frodo.postalAddress = new Address("London")

        javers.commit("author", frodo)

        when:
        (1..10).forEach( i -> {
            frodo.setSalary(1_000 * i)
            javers.commit("author", frodo)
        })
        def query = QueryBuilder.byInstanceId("Frodo", Employee.class).limit(limit).snapshotQueryLimit(snapshotQueryLimit).build()

        Stream<Shadow<Employee>> shadowsStream = javers.findShadowsAndStream(query)
        List<Shadow<Employee>> shadowsList = javers.findShadows(query)

        then:
        shadowsStream.count() == expectedSize
        shadowsList.size() == expectedSize

        where:
        expectedSize <<       [ 11,  10,   11,   10]
        limit <<              [100,  10,  100,   10]
        snapshotQueryLimit << [  2,   2,  100, null]
    }
}