package org.javers.core.diff.changetype.container

import spock.lang.Specification

class ValueAddedTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new ValueAdded(0, "value")
        def right = new ValueAdded(0, "value")

        expect:
        left.equals(right);
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new ValueAdded(0, "value")
        def right = new ValueAdded(0, "difference")

        expect:
        !left.equals(right)
    }
}
