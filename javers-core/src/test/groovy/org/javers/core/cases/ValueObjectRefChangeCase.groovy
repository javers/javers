package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.object.ValueObjectId
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
        def diff = javers.compare(new Employee(id: "1"),
                                  new Employee(id: "1", address:new Address(city: "Berlin", street: "x")))

        diff.changes.forEach{println it.toString() + " at:" + it.affectedGlobalId}

        then:
        diff.changes.size() == 2

        diff.getChangesByType(NewObject).size() == 1
        def valueChanges = diff.getChangesByType(ValueChange)
        valueChanges.size() == 1
        valueChanges[0].left == null
        valueChanges[0].right instanceof ValueObjectId
        valueChanges[0].right.value().endsWith('Case$Employee/1#address')
    }
}
