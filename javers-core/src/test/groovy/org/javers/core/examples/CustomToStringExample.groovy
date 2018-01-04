package org.javers.core.examples

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.object.InstanceId
import spock.lang.Specification

class CustomToStringExample extends Specification {

    @TypeName("Person")
    class Person {
        @Id String name
        String position
    }

    def "should map Person as EntityType"(){
      given:
      def bob = new Person(name: "Bob", position: "dev")
      def javers = JaversBuilder.javers().build()

      def personType = javers.getTypeMapping(Person)
      def bobId = personType.createIdFromInstance(bob)

      expect:
      println "JaversType of Person: " + personType.prettyPrint()

      println "Id of bob: '${bobId.value()}'"

      bobId.value() == "Person/Bob"
      bobId instanceof InstanceId
    }

    @TypeName("Entity")
    class Entity {
        @Id Point id
        String data
    }

    class Point {
        double x
        double y

        String myToString() {
            "("+ (int)x +"," +(int)y + ")"
        }
    }

    def "should use String representation of complex Id instead of its equals()"(){
      given:
      Point p1 = new Point(x: 1, y: 3)
      Point p2 = new Point(x: 1, y: 3)

      Entity entity1 = new Entity(id: p1)
      Entity entity2 = new Entity(id: p2)

      def javers = JaversBuilder.javers().build()

      expect:
      println "p1.equals(p2): " + p1.equals(p2)
      println "GlobalId of entity1: '${javers.getTypeMapping(Entity).createIdFromInstance(entity1).value()}'"
      println "GlobalId of entity2: '${javers.getTypeMapping(Entity).createIdFromInstance(entity2).value()}'"

      !p1.equals(p2)
      javers.compare(entity1, entity2).changes.size() == 0
    }

    def "should use custom toString function for complex Id"(){
      given:
      Entity entity = new Entity(
              id: new Point(x: 1/3, y: 4/3))

      when: "default reflectiveToString function"
      def javers = JaversBuilder.javers()
              .build()
      GlobalId id = javers.getTypeMapping(Entity).createIdFromInstance(entity)

      then:
      id.value() == "Entity/0.3333333333,1.3333333333"

      when: "custom toString function"
      javers = JaversBuilder.javers()
              .registerValueWithCustomToString(Point, {it.myToString()})
              .build()
      id = javers.getTypeMapping(Entity).createIdFromInstance(entity)

      then:
      id.value() == "Entity/(0,1)"
    }
}
