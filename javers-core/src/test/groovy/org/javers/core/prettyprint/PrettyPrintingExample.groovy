package org.javers.core.prettyprint

import org.javers.core.JaversBuilder
import org.javers.core.JaversCoreProperties
import org.javers.core.changelog.SimpleTextChangeLog
import org.javers.core.examples.model.Address
import org.javers.core.examples.model.Employee
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import java.time.ZonedDateTime

class PrettyPrintingExample extends Specification {

    def "should use given date format when printing"(){
      given:
      def p = new JaversCoreProperties.PrettyPrintDateFormats()
      p.setZonedDateTime("dd.mm.yyyy HH:mm")

      def javers = JaversBuilder.javers().withPrettyPrintDateFormats(p).build()
      //def javers = JaversBuilder.javers().build()

      Employee oldFrodo = new Employee(
              name:"Frodo",
              salary: 10_000,
              primaryAddress: new Address("Shire"),
              skills:["management"],
              performance: [1: "bb", 3: "aa"]
      )
      javers.commit("author", oldFrodo)

      Employee newFrodo = new Employee(
              name:"Frodo",
              position: "Hero",
              salary: 12_000,
              primaryAddress: new Address("Mordor","Some Street"),
              postalAddress: new Address("Shire"),
              skills:["management", "agile coaching"],
              lastPromotionDate: ZonedDateTime.now(),
              subordinates: [new Employee("Sam")],
              performance: [1: "aa", 2: "bb"]
      )
      javers.commit("author", newFrodo)

      when:
      def diff = javers.compare(oldFrodo, newFrodo)
      println( diff )

      println "-- SimpleTextChangeLog print --"

      def changes = javers.findChanges(QueryBuilder.byInstance(newFrodo)
              .withChildValueObjects()
              .withNewObjectChanges().build())
      println "SimpleTextChangeLog"
      def changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
      println( changeLog )

      then:
      true
    }
}
