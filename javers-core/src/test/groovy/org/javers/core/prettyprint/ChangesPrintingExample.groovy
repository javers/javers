package org.javers.core.prettyprint

import org.javers.core.JaversBuilder
import org.javers.core.changelog.SimpleTextChangeLog
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.core.examples.model.EmployeeBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChangesPrintingExample extends Specification {

    def "should use given date format when printing"(){
      given:
      def javers = JaversBuilder.javers().build()

      Employee oldFrodo = new Employee(
              name:"Frodo",
              salary: 10_000,
              primaryAddress: new Address("Shire"),
              skills:["management"]
      )
      javers.commit("author", oldFrodo)

      Employee newFrodo = new Employee(
              name:"Frodo",
              position: "Hero",
              salary: 12_000,
              primaryAddress: new Address("Mordor"),
              postalAddress: new Address("Shire"),
              skills:["management", "agile coaching"],
              lastPromotionDate: LocalDateTime.now(),
              subordinates: [new Employee("Sam")]
      )
      javers.commit("author", newFrodo)

      when:
      println "diff pretty print"
      def diff = javers.compare(oldFrodo, newFrodo)
      println( diff )

      println "--"

      def changes = javers.findChanges(QueryBuilder.byInstance(newFrodo)
              .withChildValueObjects()
              .withNewObjectChanges().build())
      println "SimpleTextChangeLog"
      def changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
      println( changeLog )


      DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      ZonedDateTime dtz = ZonedDateTime.now()
      LocalDateTime dt = LocalDateTime.now()
     // LocalDate d = LocalDate.now()

      println "dtz " + dtz.format(DEFAULT_DATE_FORMATTER)
      println "dt " + dt.format(DEFAULT_DATE_FORMATTER)
     //print d.format(DEFAULT_DATE_FORMATTER)


        then:
      true
    }
}
