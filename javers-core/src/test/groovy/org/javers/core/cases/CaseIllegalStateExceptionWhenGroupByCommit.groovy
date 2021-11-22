package org.javers.core.cases

import org.javers.core.JaversBuilder
import spock.lang.Specification

class MyClass{
    String value
}

/**
 * https://github.com/javers/javers/issues/1160
 */
class CaseIllegalStateExceptionWhenGroupByCommit extends Specification {

    def "should groupByCommit" () {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def diff = javers.compare(new MyClass(value: "abc"), new MyClass(value: "cde"))

        then:
        println (diff.changes.prettyPrint() )
        diff.changes
    }
}
