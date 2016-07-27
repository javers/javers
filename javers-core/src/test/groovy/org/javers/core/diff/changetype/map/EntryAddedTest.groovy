package org.javers.core.diff.changetype.map

import spock.lang.Specification

class EntryAddedTest extends Specification {

    def "should recognise instances are equal"() {
        given:
        def left = new EntryAdded("key", "value");
        def right = new EntryAdded("key", "value")

        when:
        def equal = left.equals(right);

        then:
        equal
    }

    def "should recognise instances are unequal"() {
        given:
        def left = new EntryAdded("key", "value");
        def right = new EntryAdded("key", "difference")

        when:
        def equal = left.equals(right);

        then:
        !equal
    }
}
