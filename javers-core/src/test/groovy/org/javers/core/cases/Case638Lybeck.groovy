package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.metamodel.object.InstanceId
import spock.lang.Specification

import javax.persistence.Id

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

    def "should handle null as Map value when Map key is an Entity"() {
      given:
      def object1 = new DataObject(id:1, map: [:])

      def object2 = new DataObject(id:1, map: [(new KeyObject(2, 3)) : null])

      def javers = JaversBuilder.javers().build()

      when:
      def diff = javers.compare(object1, object2)
      println("object diff = " + diff)


      def change = diff.getChangesByType(MapChange)[0]
      def key = change.entryChanges[0].key
      def val = change.entryChanges[0].value

      then:
      key instanceof InstanceId
      val == null
    }
}
