package org.javers.core.examples

import org.javers.common.date.FakeDateProvider
import org.javers.core.JaversBuilder
import org.javers.core.commit.CommitId
import org.javers.core.diff.changetype.NewObject
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.core.examples.model.Person
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import org.joda.time.LocalDate
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class JqlExample extends Specification {

    def "should query for Entity changes by instance Id"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit("author", new Employee(name:"bob", age:30, salary:1000) )
        javers.commit("author", new Employee(name:"bob", age:31, salary:1200) )
        javers.commit("author", new Employee(name:"john",age:25) )

        when:
        def changes = javers.findChanges( QueryBuilder.byInstanceId("bob", Employee.class).build() )

        then:
        printChanges(changes)
        assert changes.size() == 2
    }

    def "should query for ValueObject changes by owning Entity instance and class"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit("author", new Employee(name:"bob",  postalAddress:  new Address(city:"Paris")))
        javers.commit("author", new Employee(name:"bob",  primaryAddress: new Address(city:"London")))
        javers.commit("author", new Employee(name:"bob",  primaryAddress: new Address(city:"Paris")))
        javers.commit("author", new Employee(name:"lucy", primaryAddress: new Address(city:"New York")))
        javers.commit("author", new Employee(name:"lucy", primaryAddress: new Address(city:"Washington")))

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

    def "should query for ValueObject changes when stored in a List"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit("author",
                new Person(login: "bob", addresses: [new Address(city: "London"), new Address(city: "Luton")]))
        javers.commit("author",
                new Person(login: "bob", addresses: [new Address(city: "Paris"), new Address(city: "Luton")]))

        when:
        def changes = javers
                .findChanges(QueryBuilder.byValueObjectId("bob",Person.class, "addresses/0").build())

        then:
        printChanges(changes)
        assert changes.size() == 1
    }

    def "should query for ValueObject changes when stored as Map values"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit("author", new Person(login: "bob", addressMap: ["HOME":new Address(city: "Paris")]))
        javers.commit("author", new Person(login: "bob", addressMap: ["HOME":new Address(city: "London")]))

        when:
        def changes = javers
            .findChanges(QueryBuilder.byValueObjectId("bob", Person.class, "addressMap/HOME").build())

        then:
        printChanges(changes)
        assert changes.size() == 1
    }

    def "should query for Object changes by its class"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit("author", new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city: "London")))
        javers.commit("author", new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city: "Paris")))
        javers.commit("author", new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "New York")))
        javers.commit("author", new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Washington")))

        when:
        def changes = javers.findChanges( QueryBuilder.byClass(DummyAddress.class).build() )

        then:
        printChanges(changes)
        assert changes.size() == 2
    }

    def "should query for Entity changes by instance Id with property filter"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new Employee(name:"bob", age:30, salary:1000) )
        javers.commit( "author", new Employee(name:"bob", age:31, salary:1000) )
        javers.commit( "author", new Employee(name:"bob", age:31, salary:1200) )

        when:
        def changes = javers.findChanges( QueryBuilder.byInstanceId("bob", Employee.class)
                .andProperty("salary").build() )

        then:
        printChanges(changes)
        assert changes.size() == 1
    }

    def "should query for changes with limit filter"() {
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

    def "should query for changes with skip filter"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new Employee(name:"bob", age:29, salary: 900) )
        javers.commit( "author", new Employee(name:"bob", age:30, salary: 1000) )
        javers.commit( "author", new Employee(name:"bob", age:31, salary: 1100) )
        javers.commit( "author", new Employee(name:"bob", age:32, salary: 1200) )

        when:
        def changes = javers
            .findChanges( QueryBuilder.byInstanceId("bob", Employee.class).skip(1).build() )

        then:
        printChanges(changes)
        assert changes.size() == 4
    }

    def "should query for changes with commitDate filter"(){
      given:
      def fakeDateProvider = new FakeDateProvider()
      def javers = JaversBuilder.javers().withDateTimeProvider(fakeDateProvider).build()

      (0..5).each{ i ->
          def now = new LocalDate(2015+i,01,1)
          fakeDateProvider.set( now )
          def bob = new Employee(name:"bob", age:20+i)
          javers.commit("author", bob)
          println "comitting bob on $now"
      }

      when:
      def changes = javers
              .findChanges( QueryBuilder.byInstanceId("bob", Employee.class)
              .from(new LocalDate(2016,01,1))
              .to  (new LocalDate(2018,01,1)).build() )

      then:
      assert changes.size() == 2

      println "found changes:"
      changes.each {
          println "commitDate: "+ it.commitMetadata.get().commitDate+" "+it
      }
    }

    def "should query for snapshots with commitId filter"(){
        given:
        def javers = JaversBuilder.javers().build()

        (1..3).each {
            javers.commit("author", new Employee(name: "john",age: 20+it))
            javers.commit("author", new Employee(name: "bob", age: 20+it, salary: 900 + it*100))
        }

        when:
        def snapshots = javers
            .findSnapshots( QueryBuilder.byInstanceId("bob", Employee.class)
            .withCommitId(CommitId.valueOf(4)).build() )

        then:
        assert snapshots.size() == 1
        assert snapshots[0].getPropertyValue("age") == 22

        println "found snapshot:"
        println snapshots[0]
    }

    def "should query for snapshots with version filter"(){
        given:
        def javers = JaversBuilder.javers().build()

        (1..5).each {
            javers.commit("author", new Employee(name: "john",age: 20+it))
            javers.commit("author", new Employee(name: "bob", age: 20+it, salary: 900 + it*100))
        }

        when:
        def snapshots = javers
                .findSnapshots( QueryBuilder.byInstanceId("bob", Employee.class)
                .withVersion(4).build() )

        then:
        assert snapshots.size() == 1
        assert snapshots[0].getPropertyValue("age") == 24

        println "found snapshot:"
        println snapshots[0]
    }

    def "should query for changes with NewObject filter"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new Employee(name:"bob", age:30, salary: 1000) )
        javers.commit( "author", new Employee(name:"bob", age:30, salary: 1200) )

        when:
        def changes = javers
            .findChanges( QueryBuilder.byInstanceId("bob", Employee.class)
            .withNewObjectChanges(true).build() )

        then:
        printChanges(changes)
        assert changes.size() == 5
        assert changes[4] instanceof NewObject
    }

    def printChanges(def changes){
        def i = 0
        changes.each {println "commit "+ it.commitMetadata.get().id.toString()+": $it"; i++}
    }
}
