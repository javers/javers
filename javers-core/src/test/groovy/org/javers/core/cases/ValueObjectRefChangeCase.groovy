package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

class ValueObjectRefChangeCase extends Specification {

    class Employee{
        @Id String id
        Address address
    }

    class Address{
        private String city
        private String street
    }

    def "should generate ValueChange and not ReferenceChange when null is changed to ValueObject"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def diff = javers.compare(new Employee(id: "1", address:null),
                                  new Employee(id: "1", address:new Address(city: "Berlin", street: "x")))

        println diff.prettyPrint()

        then:
        diff.changes.size() == 2

        diff.changes.forEach {
            assert it instanceof ValueChange
            assert it.left == null
            assert it.right != null
        }
    }
}
