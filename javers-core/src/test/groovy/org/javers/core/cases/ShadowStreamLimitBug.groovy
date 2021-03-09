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
    def "should find shadows and stream when expectedSize = #expectedSize, limit = #limit, and snapshotQueryLimit = #snapshotQueryLimit" () {
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
        println "shadowsStream query " + query

        List<Shadow<Employee>> shadowsList = javers.findShadows(query)
        println "shadowsList query " + query

        then:
        shadowsStream.count() == expectedSize
        shadowsList.size() == expectedSize

        where:
        expectedSize <<       [ 11,  11,   10, 10, 11, 11]
        limit <<              [100, 100,   10, 10, 11, 11]
        snapshotQueryLimit << [  2, 100, null,  2,  2, 11]
    }
}