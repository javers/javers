package org.javers.core.examples

import groovy.transform.TupleConstructor
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class GroovyDiffExample extends Specification {

    @TupleConstructor
    class Person {
        @Id login
        String lastName
    }

    def "should calculate diff for GroovyObjects"(){
      given:
      def javers = JaversBuilder.javers().build()

      when:
      def diff = javers.compare(
          new Person('bob','Uncle'),
          new Person('bob','Martin')
      )

      then:
      diff.changes.size() == 1
      diff.changes[0].left == 'Uncle'
      diff.changes[0].right == 'Martin'
    }
}
