package org.javers.core.diff.changetype.container

import spock.lang.Specification

class ValueRemovedTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new ValueRemoved(0, "value")
        def right = new ValueRemoved(0, "value")

        expect:
        left.equals(right)
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new ValueRemoved(0, "value")
        def right = new ValueRemoved(0, "difference")

        expect:
        !left.equals(right)
    }
}
