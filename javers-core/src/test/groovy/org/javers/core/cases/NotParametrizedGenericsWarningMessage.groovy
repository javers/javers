package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/362
 *
 * @author bartosz.walacik
 */
class NotParametrizedGenericsWarningMessage extends Specification{

    class Person<T> {
        @Id int id = 1
        Map<T, T[]> friends
        Set<T> skills
        List<T> colors
    }

    def "should not fail on overcomplicated generic properties"(){
        given:
        def javers = JaversBuilder.javers().build()
        def person1 = new Person(friends: [:], skills: [], colors:[])
        def person2 = new Person(friends: ["a":"b"], skills:["a"], colors:["a"])

        when:
        def diff = javers.compare(person1, person2)

        then:
        diff.changes.size() == 3
    }
}
