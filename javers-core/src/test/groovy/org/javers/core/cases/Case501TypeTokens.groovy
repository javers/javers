package org.javers.core.cases

import org.javers.core.JaversBuilder
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/501
 */
class Case501TypeTokens extends Specification {

    class Identified<ID> {
        ID id
    }

    class Versioned<ID, VER> extends Identified<ID> {
        VER version
    }

    class Person extends Versioned<Long, Long> {
        String name
    }

    def "should resolve type arguments"(){
      given:
      def javers = JaversBuilder.javers().build()
      def changes = javers.compare(new Person(id:1), new Person(id:1))

      expect:
      changes.changes.size() == 0
    }
}
