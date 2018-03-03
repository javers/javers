package org.javers.core.cases

import org.javers.core.JaversBuilder
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

        private static class KeyHolder {
            int n
            int m
        }
    }

    def "should handle null as Map value when Map key is an Entity"() {
      given:
      def object1 = new DataObject(id:1, map: [:])
      object1.map =  [:]

      def object2 = new DataObject(id:1, map: [{new KeyObject(1, 2)} : null])

      def javers = JaversBuilder.javers().build()

      when:
      // this compare call fails in a NullPointerException
      def diff = javers.compare(object1, object2)
      println("object diff = " + diff)

      then:
      diff.changes.size() == 1
    }
}
