package org.javers.core.examples

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.type.EntityType
import spock.lang.Specification

class CustomToStringExample extends Specification {

    @TypeName("Entity")
    class Entity {
        @Id Point id
        String data
    }

    class Point {
        double x
        double y
    }

    def "should use custom toString function for complex ID"(){
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
              .registerValueWithCustomToString(Point, {p -> "("+ (int)p.x +"," +(int)p.y + ")"})
              .build()
      id = javers.getTypeMapping(Entity).createIdFromInstance(entity)

      then:
      id.value() == "Entity/(0,1)"
    }
}
