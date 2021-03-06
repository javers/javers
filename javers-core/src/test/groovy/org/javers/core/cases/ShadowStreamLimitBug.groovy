package org.javers.core.cases


import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.examples.model.Employee
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
import spock.lang.Specification

import java.util.stream.Stream

/**
 * https://github.com/javers/javers/issues/822
 */
class ShadowStreamLimitBug extends Specification {

    def "should find shadows and stream with limit" () {
        given:
        Javers javers = JaversBuilder.javers().build()
        Employee frodo = new Employee("Frodo")
        frodo.addSubordinate(new Employee("Sam"))

        javers.commit("author", frodo)

        when:
        (1..10).forEach( i -> {
            frodo.setSalary(1_000 * i)
            javers.commit("author", frodo)
        })
        def query = QueryBuilder.byInstanceId("Frodo", Employee.class).limit(2).build()
        Stream<Shadow<Employee>> shadows = javers.findShadowsAndStream(query)

        List<Shadow<Employee>> shadowsC = javers.findShadows(query)

        then:
        shadows.count() == 11
        shadowsC.size() == 2
    }
}