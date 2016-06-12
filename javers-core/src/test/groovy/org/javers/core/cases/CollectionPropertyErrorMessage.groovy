package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/353
 *
 * @author bartosz.walacik
 */
class CollectionPropertyErrorMessage extends Specification {

    class Person {
        @Id int id = 1
        Collection<Person> friends
    }

    def "should not fail for raw collection properties"(){
      given:
      def javers = JaversBuilder.javers().build()
      def person1 = new Person(friends: [])
      def person2 = new Person(friends: [person1])

      when:
      def diff = javers.compare(person1, person2)

      then:
      diff.changes.size() == 0
      //only logger warning is expected with a message about unsupported types
    }
}
