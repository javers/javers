package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.MappingStyle
import org.javers.core.diff.Diff
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/810
 */

class Building {
    private Integer id
    private Set<Floor> floors

    @Id
    Integer getId() {
        return id
    }

    void setId(Integer id) {
        this.id = id
    }

    Set<Floor> getFloors() {
        return floors
    }

    void setFloors(Set<Floor> floors) {
        this.floors = floors
    }
}

class Floor {
    private Set<Room> rooms

    Set<Room> getRooms() {
        return rooms
    }

    void setRooms(Set<Room> rooms) {
        this.rooms = rooms
    }
}

class Room {
    private String name
    private String number

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @DiffIgnore
    String getNumber() {
        return number
    }

    void setNumber(String number) {
        this.number = number
    }
}

class Case810DiffIgnore extends Specification {

    def "should ignore containers of ValueObjects when calculating Object Hash"() {
      when:
      Javers javers = JaversBuilder.javers()
              .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
              .withMappingStyle(MappingStyle.BEAN)
              .build();


      Room room1 = new Room(name: "Room!", number:"Ignore me")
      Floor floor1 = new Floor(rooms:[room1])

      Room room2 = new Room(name: "Room!", number:"Different room")
      Floor floor2 = new Floor(rooms:[room2])

      Building building1 = new Building(id:1, floors:[floor1])
      Building building2 = new Building(id:1, floors:[floor2])

      Diff diff = javers.compare(building1, building2)

      println diff.prettyPrint()
      println diff.getChanges().size()

      then:
      diff.getChanges().size() == 0
    }
}
