package org.javers.core.diff.changetype.container

import spock.lang.Specification

class ValueAddedTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new ValueAdded(0, "value");
        def right = new ValueAdded(0, "value")

        when:
        def equal = left.equals(right);

        then:
        equal
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new ValueAdded(0, "value");
        def right = new ValueAdded(0, "difference")

        when:
        def equal = left.equals(right);

        then:
        !equal
    }
}
