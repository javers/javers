package org.javers.core.examples

import org.javers.core.JaversBuilder
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class JqlExample extends Specification {

    def "should query for Entity changes by instance Id"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new Employee(name:"bob", age:30, salary:1000) )
        javers.commit( "author", new Employee(name:"bob", age:31, salary:1200) )
        javers.commit( "author", new Employee(name:"john",age:25) )

        when: "query by instance Id"
        def changes = javers.findChanges( QueryBuilder.byInstanceId("bob", Employee.class).build() )

        then:
        printChanges(changes)
        assert changes.size() == 2

        when: "query by instance Id and property"
        changes = javers.findChanges( QueryBuilder.byInstanceId("bob", Employee.class)
            .andProperty("age").build() )

        then:
        printChanges(changes)
        assert changes.size() == 1
    }

    def "should query for changes with limit"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new Employee(name:"bob", age:29) )
        javers.commit( "author", new Employee(name:"bob", age:30, salary: 1000) )
        javers.commit( "author", new Employee(name:"bob", age:31, salary: 1100) )
        javers.commit( "author", new Employee(name:"bob", age:32, salary: 1200) )

        when:
        def changes = javers
            .findChanges( QueryBuilder.byInstanceId("bob", Employee.class).limit(3).build() )

        then:
        printChanges(changes)
        assert changes.size() == 4
    }


    def "should query for ValueObject changes by owning Entity instance and class"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new Employee(name:"bob",  postalAddress:  new Address(city:"Paris")))
        javers.commit( "author", new Employee(name:"bob",  primaryAddress: new Address(city:"London")))
        javers.commit( "author", new Employee(name:"bob",  primaryAddress: new Address(city:"Paris")))
        javers.commit( "author", new Employee(name:"lucy", primaryAddress: new Address(city:"New York")))
        javers.commit( "author", new Employee(name:"lucy", primaryAddress: new Address(city:"Washington")))

        when: "query for ValueObject changes by owning Entity instance Id"
        def changes = javers
            .findChanges( QueryBuilder.byValueObjectId("bob",Employee.class,"primaryAddress").build())

        then:
        printChanges(changes)
        assert changes.size() == 1

        when: "query for ValueObject changes by owning Entity class"
        changes = javers
            .findChanges( QueryBuilder.byValueObject(Employee.class,"primaryAddress").build())

        then:
        printChanges(changes)
        assert changes.size() == 2
    }

    def printChanges(def changes){
        def i = 0
        changes.each {println "commit "+ it.commitMetadata.get().id.toString()+": $it"; i++}
    }
}
