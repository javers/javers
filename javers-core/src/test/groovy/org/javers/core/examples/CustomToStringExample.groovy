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

    def "should use Value smartToString() function to build InstanceId"(){
      given:
      Point p1 = new Point(x: 1, y: 3)
      Point p2 = new Point(x: 1, y: 3)

      Entity entity1 = new Entity(id: p1)
      Entity entity2 = new Entity(id: p2)

      def javers = JaversBuilder.javers().build()

      expect:
      !p1.equals(p2)
      javers.compare(entity1, entity2).changes.size() == 0

      def id = javers.getTypeMapping(Entity).createIdFromInstance(entity1)
      id.value() == "Entity/1.0,3.0"
    }

    def "should use custom Value toString() function to build InstanceId"(){
      given:
      Entity entity = new Entity(id: new Point(x: 1/3, y: 4/3))

      when: "default reflectiveToString function"
      def javers = JaversBuilder.javers().build()
      GlobalId id = javers.getTypeMapping(Entity).createIdFromInstance(entity)

      then:
      id.value() == "Entity/0.3333333333,1.3333333333"

      when: "custom toString function"
      javers = JaversBuilder.javers()
              .registerValueWithCustomToString(Point, {p -> p.myToString()})
              .build()
      id = javers.getTypeMapping(Entity).createIdFromInstance(entity)

      then:
      id.value() == "Entity/(0,1)"
    }
}
