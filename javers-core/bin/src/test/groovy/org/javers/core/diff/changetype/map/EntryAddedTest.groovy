package org.javers.core.diff.changetype.map

import spock.lang.Specification

class EntryAddedTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new EntryAdded("key", "value")
        def right = new EntryAdded("key", "value")

        expect:
        left.equals(right);
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new EntryAdded("key", "value")
        def right = new EntryAdded("key", "difference")

        expect:
        !left.equals(right)
    }
}
