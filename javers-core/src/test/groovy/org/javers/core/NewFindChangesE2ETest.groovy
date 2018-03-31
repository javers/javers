package org.javers.core

import org.javers.core.diff.Change
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

class NewFindChangesE2ETest extends Specification {
    def javers = JaversBuilder.javers().build()

    def "should return changes as flat List"(){
      given:
      commitChanges()

      when:
      List<Change> changes = javers.findChanges(QueryBuilder.byClass(Employee).build())

      println "changes: "
      changes.each{ println it }

      then:
      changes.size() == 7
    }

    def "should return changes grouped by commit"(){
        given:
        commitChanges()

        when:
        Changes changes = javers.findChanges(QueryBuilder.byClass(Employee).withChildValueObjects().build())

        println changes.prettyPrint()

        List<Changes.ChangesByCommit> changesByCommit = changes.groupByCommit()

        then:
        changesByCommit.size() == 3

        changesByCommit[0].commit.id.majorId == 4
        changesByCommit[0].size() == 3

        changesByCommit[1].commit.id.majorId == 3
        changesByCommit[1].size() == 2

        changesByCommit[2].commit.id.majorId == 2
        changesByCommit[2].size() == 4
    }

    def "should return changes grouped by commit and by entity object"(){
        given:
        commitChanges()

        when:
        Changes changes = javers.findChanges(QueryBuilder.byClass(Employee).withChildValueObjects().build())

        println changes.prettyPrint()

        List<Changes.ChangesByCommit> changesByCommit = changes.groupByCommit()
        List<Changes.ChangesByObject> changesByObject = changesByCommit[0].groupByObject()

        then:
        changesByObject.size() == 1
        changesByObject[0].size() == 3
        changesByObject[0].commit.id.majorId == 4
        changesByObject[0].globalId.value() == 'Employee/kaz'

        when:
        changesByObject = changesByCommit[1].groupByObject()

        then:
        changesByObject.size() == 1
        changesByObject[0].size() == 2
        changesByObject[0].commit.id.majorId == 3
        changesByObject[0].globalId.value() == 'Employee/kaz'

        when:
        changesByObject = changesByCommit[2].groupByObject()

        then:
        changesByObject.size() == 2
        changesByObject.each {
            it.commit.id.majorId == 2
        }
        changesByObject.collect{it.globalId.value()} as Set == ['Employee/kaz','Employee/stef'] as Set
    }

    void commitChanges() {
        def kaz = new Employee(name: 'kaz', primaryAddress : new Address("one"), postalAddress: new Address("one"))
        def stef = new Employee(name: 'stef')
        kaz.addSubordinate(stef)

        javers.commit('author', kaz)

        kaz.salary = 1000
        kaz.age = 30
        stef.salary = 1500
        stef.age = 35
        javers.commit('author', kaz)

        kaz.salary = 1001
        kaz.age = 31
        javers.commit('author', kaz)

        kaz.salary = 1002
        kaz.primaryAddress = new Address("two")
        kaz.postalAddress = new Address("two")

        javers.commit('author', kaz)
    }
}
