package org.javers.core.diff.changetype.map

import spock.lang.Specification

class EntryRemovedTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new EntryRemoved("key", "value")
        def right = new EntryRemoved("key", "value")

        expect:
        left.equals(right)
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new EntryRemoved("key", "value")
        def right = new EntryRemoved("key", "difference")

        expect:
        !left.equals(right)
    }
}
