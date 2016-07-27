package org.javers.core.diff.changetype.map

import spock.lang.Specification

class EntryValueChangeTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new EntryValueChange("key", "a", "b");
        def right = new EntryValueChange("key", "a", "b")

        when:
        def equal = left.equals(right);

        then:
        equal
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new EntryValueChange("key", "a", "b");
        def right = new EntryValueChange("key", "a", "c")

        when:
        def equal = left.equals(right);

        then:
        !equal
    }
}
