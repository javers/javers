package org.javers.core.diff.changetype.map

import spock.lang.Specification

class EntryRemovedTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new EntryRemoved("key", "value");
        def right = new EntryRemoved("key", "value")

        when:
        def equal = left.equals(right);

        then:
        equal
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new EntryRemoved("key", "value");
        def right = new EntryRemoved("key", "difference")

        when:
        def equal = left.equals(right);

        then:
        !equal
    }
}
