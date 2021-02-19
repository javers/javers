package org.javers.core

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.Id
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

class NewObjectChangesE2ETest extends Specification {

    class Employee{
        @Id String id
        Address address
        Employee ref
        String name
    }

    class Address{
        private String city
        private String street
    }

    def "should generate initial ValueChanges in findChanges() when Entity is added "() {

        given:
        def javers = JaversBuilder.javers().build()

        when:
        javers.commit("me", new Employee(id: "1", name: "mine"))

        def changes = javers.findChanges(QueryBuilder.byInstanceId("1", Employee).build())

        println changes.prettyPrint()

        then:
        changes.size() == 3
        changes.getChangesByType(NewObject).size() == 1
        changes.getChangesByType(ValueChange).size() == 2
        with(changes.getChangesByType(ValueChange).find { it.propertyName == "id" }) {
            assert it.left == null
            assert it.right == "1"
        }
        with(changes.getChangesByType(ValueChange).find { it.propertyName == "name" }) {
            assert it.left == null
            assert it.right == "mine"
        }
    }

    def "should not generate initial ValueChanges in findChanges() when disabled"() {

        given:
        def javers = JaversBuilder.javers()
                .withNewObjectChanges(false)
                .build()

        when:
        javers.commit("me", new Employee(id: "1", name: "mine"))

        def changes = javers.findChanges(QueryBuilder.byInstanceId("1", Employee).build())

        println changes.prettyPrint()

        then:
        changes.size() == 1
        changes.getChangesByType(NewObject).size() == 1
    }

    def "should not generate terminal ValueChanges in findChanges() when disabled"() {
        given:
        def javers = JaversBuilder
                .javers().withRemovedObjectChanges(false)
                .build()

        when:
        javers.commit("me", new Employee(id: "1", name:"mine"))
        javers.commitShallowDelete("me", new Employee(id: "1", name:"mine"))

        def changes = javers
                .findChanges( QueryBuilder.byInstanceId("1", Employee).build() )

        println changes.prettyPrint()
        def lastCommitChanges = changes.findAll{it.commitMetadata.get().id.majorId==2}

        then:
        lastCommitChanges.size() == 1
        lastCommitChanges[0] instanceof ObjectRemoved
    }

    def "should generate terminal ValueChanges in findChanges() when Entity is removed " () {
        given:
        def javers = JaversBuilder
                .javers()
                .build()

        when:
        javers.commit("me", new Employee(id: "1", name:"mine"))
        javers.commitShallowDelete("me", new Employee(id: "1", name:"mine"))

        def changes = javers
                .findChanges( QueryBuilder.byInstanceId("1", Employee)
                .build() )

        println changes.prettyPrint()
        def lastCommitChanges = changes.findAll{it.commitMetadata.get().id.majorId==2}
        def lastCommitValueChanges = lastCommitChanges.findAll{it instanceof ValueChange}

        then:
        lastCommitChanges.size() == 3
        lastCommitValueChanges.size() == 2
        with(lastCommitValueChanges.find { it.propertyName == "id"}) {
            assert it.left == "1"
            assert it.right == null
        }
        with(lastCommitValueChanges.find {it.propertyName == "name"}) {
            assert it.left == "mine"
            assert it.right == null
        }
    }

    def "should generate initial ValueChanges in compare() when null is changed to Entity" () {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def diff = javers.compare(
                new Employee(id: "1"),
                new Employee(id: "1", ref: new Employee(id: "2", name:"mine")))

        println diff.prettyPrint()

        then:
        diff.changes.size() == 4

        with(diff.getChangesByType(NewObject)[0]) {
            assert it.affectedGlobalId.value() == 'org.javers.core.NewObjectChangesE2ETest$Employee/2'
        }

        with(diff.getChangesByType(ReferenceChange)[0]) {
            assert it.left == null
            assert it.right.value() == 'org.javers.core.NewObjectChangesE2ETest$Employee/2'
        }

        diff.getChangesByType(ValueChange).forEach {
            assert it.left == null
            assert it.right != null
        }

        with(diff.getChangesByType(ValueChange).find {it.propertyName == "id"}) {
            assert it.left == null
            assert it.right == "2"
        }

        with(diff.getChangesByType(ValueChange).find {it.propertyName == "name"}) {
            assert it.left == null
            assert it.right == "mine"
        }
    }

    def "should not generate initial ValueChanges in compare() when disabled" () {
        given:
        def javers = JaversBuilder.javers().withNewObjectChanges(false).build()

        when:
        def diff = javers.compare(
                new Employee(id: "1"),
                new Employee(id: "1", ref: new Employee(id: "2", name:"mine")))

        then:
        diff.changes.size() == 2
        diff.changes.getChangesByType(ReferenceChange).size() == 1
        diff.changes.getChangesByType(NewObject).size() == 1
    }

    def "should not generate terminal ValueChanges in compare() when disabled" () {
        given:
        def javers = JaversBuilder.javers().withRemovedObjectChanges(false).build()

        when:
        def diff = javers.compare(
                new Employee(id: "1", ref: new Employee(id: "2", name:"mine")),
                new Employee(id: "1"))

        then:
        diff.changes.size() == 2
        diff.changes.getChangesByType(ReferenceChange).size() == 1
        diff.changes.getChangesByType(ObjectRemoved).size() == 1
    }

    def "should generate terminal ValueChanges in compare() when Entity is changed to null" () {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def diff = javers.compare(
                new Employee(id: "1", ref: new Employee(id: "2", name:"mine")),
                new Employee(id: "1"))

        println diff.prettyPrint()

        then:
        diff.changes.size() == 4

        with(diff.getChangesByType(ObjectRemoved)[0]) {
            assert it.affectedGlobalId.value() == 'org.javers.core.NewObjectChangesE2ETest$Employee/2'
        }

        with(diff.getChangesByType(ReferenceChange)[0]) {
            assert it.left.value() == 'org.javers.core.NewObjectChangesE2ETest$Employee/2'
            assert it.right == null
        }

        diff.getChangesByType(ValueChange).forEach {
            assert it.left != null
            assert it.right == null
        }

        with(diff.getChangesByType(ValueChange).find {it.propertyName == "id"}) {
            assert it.left == "2"
            assert it.right == null
        }

        with(diff.getChangesByType(ValueChange).find {it.propertyName == "name"}) {
            assert it.left == "mine"
            assert it.right == null
        }
    }

    def "should generate ValueChanges in compare() when null is changed to ValueObject"() {
        given:
        def javers = JaversBuilder
                .javers()
                .build()

        when:
        def diff = javers.compare(new Employee(id: "1", address: null),
                                  new Employee(id: "1", address: new Address(street: "x", city:"Paris")))

        println diff.prettyPrint()

        then:
        diff.changes.size() == 3
        diff.getChangesByType(ValueChange).size() == 2

        with(diff.getChangesByType(NewObject)[0]) {
            assert it.affectedGlobalId.value() == 'org.javers.core.NewObjectChangesE2ETest$Employee/1#address'
        }

        with(diff.getChangesByType(ValueChange).find {it.propertyName == "street"}) {
            assert it.left == null
            assert it.right == "x"
        }
        with(diff.getChangesByType(ValueChange).find {it.propertyName == "city"}) {
            assert it.left == null
            assert it.right == "Paris"
        }
    }

    def "should generate ValueChanges in compare() when ValueObject is changed to null"() {
        given:
        def javers = JaversBuilder
                .javers()
                .build()

        when:
        def diff = javers.compare(
                new Employee(id: "1", address:new Address(city: "Berlin")),
                new Employee(id: "1", address:null))

        println diff.prettyPrint()

        then:
        diff.changes.size() == 2

        with(diff.getChangesByType(ObjectRemoved)[0]) {
            assert it.affectedGlobalId.value() == 'org.javers.core.NewObjectChangesE2ETest$Employee/1#address'
        }

        with(diff.getChangesByType(ValueChange)[0]) {
            assert it.propertyName == "city"
            assert it.left == "Berlin"
            assert it.right == null
        }
    }
}
