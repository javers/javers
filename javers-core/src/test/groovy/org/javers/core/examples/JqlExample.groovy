package org.javers.core.examples

import org.javers.common.date.FakeDateProvider
import org.javers.core.JaversBuilder
import org.javers.core.commit.CommitId
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.core.examples.model.Person
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import java.time.LocalDate

/**
 * @author bartosz.walacik
 */
class JqlExample extends Specification {

    def "should query for Changes made on any object"() {
        given:
        def javers = JaversBuilder.javers().build()
        def bob = new Employee(name: "bob",
                               salary: 1000,
                               primaryAddress: new Address("London"))
        javers.commit("author", bob)       // initial commit

        bob.salary = 1200                  // changes
        bob.primaryAddress.city = "Paris"  //
        javers.commit("author", bob)       // second commit

        when:
        def changes = javers.findChanges( QueryBuilder.anyDomainObject().build() )

        then:
        assert changes.size() == 2
        ValueChange salaryChange = changes.find{it.propertyName == "salary"}
        ValueChange cityChange = changes.find{it.propertyName == "city"}
        assert salaryChange.left ==  1000
        assert salaryChange.right == 1200
        assert cityChange.left ==  "London"
        assert cityChange.right == "Paris"

        printChanges(changes)
    }

    def "should query for Shadows of an object"() {
        given:
        def javers = JaversBuilder.javers().build()
        def bob = new Employee(name: "bob",
                               salary: 1000,
                               primaryAddress: new Address("London"))
        javers.commit("author", bob)       // initial commit

        bob.salary = 1200                  // changes
        bob.primaryAddress.city = "Paris"  //
        javers.commit("author", bob)       // second commit

        when:
        def shadows = javers.findShadows(
                QueryBuilder.byInstance(bob).withChildValueObjects().build() )

        then:
        assert shadows.size() == 2

        Employee bobNew = shadows[0].get()     // Employees Shadows are instances
        Employee bobOld = shadows[1].get()     // of Employee.class

        bobNew.salary == 1200
        bobOld.salary == 1000
        bobNew.primaryAddress.city == "Paris"  // Employees Shadows are linked
        bobOld.primaryAddress.city == "London" // to Addresses Shadows

        shadows[0].commitMetadata.id.majorId == 2
        shadows[1].commitMetadata.id.majorId == 1
    }

    def "should query for Shadows with different scopes"(){
      given:
      def javers = JaversBuilder.javers().build()
      def john = new Employee(name: "john")
      def bob = new Employee(name: "bob", boss: john)

      javers.commit("author", bob)       // initial commit
      bob.salary = 1200                  // changes
      javers.commit("author", bob)       // second commit

      when: "query with SHALLOW scope"
      def shadows = javers.findShadows(QueryBuilder.byInstance(bob).build() ) //SHALLOW scope
      Employee bobNew = shadows[0].get()
      Employee bobOld = shadows[1].get()

      then:
      assert bobNew.boss == null  //john is outside the query scope,
      assert bobOld.boss == null  //so references from bob to john are nulled

      when: "query with COMMIT_DEPTH scope"
      shadows = javers.findShadows(QueryBuilder.byInstance(bob).withShadowScopeDeep().build())
      bobNew = shadows[0].get()
      bobOld = shadows[1].get()

      then:
      assert bobNew.boss.name == "john"  // john is inside the query scope,
      assert bobOld.boss.name == "john"  // so his Shadow is reconstruced
                                         // and linked with bob's Shadows
    }

    def "should query for Snapshots of an object"(){
        given:
        def javers = JaversBuilder.javers().build()
        def bob = new Employee(name: "bob",
                               salary: 1000,
                               age: 29,
                               boss: new Employee("john"))
        javers.commit("author", bob)       // initial commit

        bob.salary = 1200                  // changes
        bob.age = 30                       //
        javers.commit("author", bob)       // second commit

        when:
        def snapshots = javers.findSnapshots( QueryBuilder.byInstance(bob).build() )

        then:
        assert snapshots.size() == 2

        assert snapshots[0].commitMetadata.id.majorId == 2
        assert snapshots[0].changed == ["salary", "age"]
        assert snapshots[0].getPropertyValue("salary") == 1200
        assert snapshots[0].getPropertyValue("age") == 30
        // references are dehydrated
        assert snapshots[0].getPropertyValue("boss").value() == "Employee/john"

        assert snapshots[1].commitMetadata.id.majorId == 1
        assert snapshots[1].getPropertyValue("salary") == 1000
        assert snapshots[1].getPropertyValue("age") == 29
        assert snapshots[1].getPropertyValue("boss").value() == "Employee/john"
    }


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

        javers.commit("me", new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city: "London")))
        javers.commit("me", new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city: "Paris")))
        javers.commit("me", new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Rome")))
        javers.commit("me", new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Palma")))
        javers.commit("me", new SnapshotEntity(id:2, intProperty:2))

        when:
        def changes = javers.findChanges( QueryBuilder.byClass(DummyAddress.class).build() )

        then:
        printChanges(changes)
        assert changes.size() == 2
    }

    def "should query for any domain object changes"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit("author", new Employee(name:"bob", age:30) )
        javers.commit("author", new Employee(name:"bob", age:31) )
        javers.commit("author", new DummyUserDetails(id:1, someValue:"old") )
        javers.commit("author", new DummyUserDetails(id:1, someValue:"new") )

        when:
        def changes = javers.findChanges( QueryBuilder.anyDomainObject().build() )

        then:
        printChanges(changes)
        assert changes.size() == 2
    }

    def "should query for changes (and snapshots) with property filter"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit("author", new Employee(name:"bob", age:30, salary:1000) )
        javers.commit("author", new Employee(name:"bob", age:31, salary:1100) )
        javers.commit("author", new Employee(name:"bob", age:31, salary:1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class)
                .andProperty("salary").build()
        def changes = javers.findChanges(query)

        then:
        printChanges(changes)
        assert changes.size() == 2
        assert javers.findSnapshots(query).size() == 3
    }

    def "should query for changes (and snapshots) with limit filter"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new Employee(name:"bob", salary: 900) )
        javers.commit( "author", new Employee(name:"bob", salary: 1000) )
        javers.commit( "author", new Employee(name:"bob", salary: 1100) )
        javers.commit( "author", new Employee(name:"bob", salary: 1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class).limit(2).build()
        def changes = javers.findChanges(query)

        then:
        printChanges(changes)
        assert changes.size() == 2
        assert javers.findSnapshots(query).size() == 2
    }

    def "should query for changes (and snapshots) with skip filter"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new Employee(name:"bob", age:29, salary: 900) )
        javers.commit( "author", new Employee(name:"bob", age:30, salary: 1000) )
        javers.commit( "author", new Employee(name:"bob", age:31, salary: 1100) )
        javers.commit( "author", new Employee(name:"bob", age:32, salary: 1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class).skip(1).build()
        def changes = javers.findChanges( query )

        then:
        printChanges(changes)
        assert changes.size() == 4
        assert javers.findSnapshots(query).size() == 3
    }

    def "should query for changes (and snapshots) with author filter"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "Jim", new Employee(name:"bob", age:29, salary: 900) )
        javers.commit( "Pam", new Employee(name:"bob", age:30, salary: 1000) )
        javers.commit( "Jim", new Employee(name:"bob", age:31, salary: 1100) )
        javers.commit( "Pam", new Employee(name:"bob", age:32, salary: 1200) )

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class).byAuthor("Pam").build()
        def changes = javers.findChanges( query )

        then:
        printChanges(changes)
        assert changes.size() == 4
        assert javers.findSnapshots(query).size() == 2
    }

    def "should query for changes (and snapshots) with commit property filters"() {
        given:
        def javers = JaversBuilder.javers().build()

        def bob = new Employee(name: "bob", position: "Assistant", salary: 900)
        javers.commit( "author", bob, ["tenant": "ACME", "event": "birthday"] )
        bob.position = "Specialist"
        bob.salary = 1600
        javers.commit( "author", bob, ["tenant": "ACME", "event": "promotion"] )

        def pam = new Employee(name: "pam", position: "Secretary", salary: 1300)
        javers.commit( "author", pam, ["tenant": "Dunder Mifflin", "event": "hire"] )
        bob.position = "Saleswoman"
        bob.salary = 1700
        javers.commit( "author", pam, ["tenant": "Dunder Mifflin", "event": "promotion"] )

        when:
        def query = QueryBuilder.anyDomainObject()
            .withCommitProperty("tenant", "ACME")
            .withCommitProperty("event", "promotion").build()
        def changes = javers.findChanges( query )

        then:
        printChanges(changes)
        assert changes.size() == 2
        assert javers.findSnapshots(query).size() == 1
    }

    def "should query for changes (and snapshots) with commitDate filter"(){
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
      def query = QueryBuilder.byInstanceId("bob", Employee.class)
              .from(new LocalDate(2016,01,1))
              .to  (new LocalDate(2018,01,1)).build()
      def changes = javers.findChanges( query )

      then:
      assert changes.size() == 3
      assert javers.findSnapshots(query).size() == 3

      println "found changes:"
      changes.each {
          println "commitDate: "+ it.commitMetadata.get().commitDate+" "+it
      }
    }

    def "should query for changes (and snapshots) with commitId filter"(){
        given:
        def javers = JaversBuilder.javers().build()

        (1..3).each {
            javers.commit("author", new Employee(name:"john", age:20+it))
            javers.commit("author", new Employee(name:"bob",  age:20+it))
        }

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class )
                .withCommitId( CommitId.valueOf(4) ).build()
        def changes = javers.findChanges(query)

        then:
        printChanges(changes)
        assert changes.size() == 1
        assert changes[0].left == 21
        assert changes[0].right == 22
        assert javers.findSnapshots(query).size() == 1
    }

    def "should query for changes (and snapshots) with version filter"(){
        given:
        def javers = JaversBuilder.javers().build()

        (1..5).each {
            javers.commit("author", new Employee(name: "john",age: 20+it))
            javers.commit("author", new Employee(name: "bob", age: 20+it))
        }

        when:
        def query = QueryBuilder.byInstanceId("bob", Employee.class).withVersion(4).build()
        def changes = javers.findChanges( query )

        then:
        printChanges(changes)
        assert changes.size() == 1
        assert changes[0].left == 23
        assert changes[0].right == 24
        assert javers.findSnapshots(query).size() == 1
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

    def "should query for changes made on Entity and its ValueObjects by InstanceId and Class"(){
      given:
      def javers = JaversBuilder.javers().build()

      def bob = new Employee(name:"bob", age:30, salary: 1000,
              primaryAddress: new Address(city:"Paris"),
              postalAddress: new Address(city:"Paris"))
      javers.commit("author", bob)

      bob.age = 31
      bob.primaryAddress.city = "London"
      javers.commit("author", bob)

      when: "query by instance Id"
      def query = QueryBuilder.byInstanceId("bob", Employee.class).withChildValueObjects().build()
      def changes = javers.findChanges( query )

      then:
      printChanges(changes)
      assert changes.size() == 2

      when: "query by Entity class"
      query = QueryBuilder.byClass(Employee.class).withChildValueObjects().build()
      changes = javers.findChanges( query )

      then:
      printChanges(changes)
      assert changes.size() == 2
    }

    def printChanges(def changes){
        println "changes:"
        def i = 0
        changes.each {println "commit "+ it.commitMetadata.get().id.toString()+": $it"; i++}
    }
}
