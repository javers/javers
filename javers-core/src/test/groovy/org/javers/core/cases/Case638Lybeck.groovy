package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.metamodel.object.InstanceId
import spock.lang.Specification

import jakarta.persistence.Id

class Case638Lybeck extends Specification {

    class DataObject {
        @Id
        int id
        Map<KeyObject, String> map
    }

    class KeyObject {
        @Id
        KeyHolder key

        KeyObject(int n, int m) {
            this.key = new KeyHolder(n:n, m:m)
        }
    }

    class KeyHolder {
        int n
        int m

        @Override
        String toString() {
            return n +" "+ m
        }
    }

    def "should treat entry with null value as no entry when Map key is an Entity"() {
      given:
      def object1 = new DataObject(id:1, map: [:])
      def object2 = new DataObject(id:1, map: [(new KeyObject(2, 3)) : null])

      def javers = JaversBuilder.javers().withInitialChanges(false).build()

      when:
      def diff = javers.compare(object1, object2)
      println("object diff = " + diff)

      then:
      diff.changes.size() == 1
      diff.changes[0] instanceof NewObject
    }
}
