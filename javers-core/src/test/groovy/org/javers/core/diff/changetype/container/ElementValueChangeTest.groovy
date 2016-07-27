package org.javers.core.diff.changetype.container

import spock.lang.Specification

class ElementValueChangeTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new ElementValueChange(0, "a", "b")
        def right = new ElementValueChange(0, "a", "b")

        expect:
        left.equals(right)
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new ElementValueChange(0, "a", "b")
        def right = new ElementValueChange(0, "a", "c")

        expect:
        !left.equals(right)
    }
}
