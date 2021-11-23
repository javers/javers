package org.javers.core.diff.appenders


import org.javers.core.JaversBuilder
import org.javers.core.diff.custom.NullAsBlankStringComparator
import org.javers.core.model.DummyUserWithValues
import spock.lang.Specification
import spock.lang.Unroll

class CustomNullHandlingValueComparatorTest extends Specification {

    @Unroll
    def "should treat blank Strings and nulls as equal if NullAsBlankStringComparator is used when left, right are : #left, #right"() {

        given:
        def javers = JaversBuilder.javers()
                .registerValue(String, new NullAsBlankStringComparator()).build()

        when:
        def diff = javers.compare(
                DummyUserWithValues.dummyUserWithPosition("1", left),
                DummyUserWithValues.dummyUserWithPosition("1", right))

        then:
        diff.changes.size() == expectedChange

        where:
        left | right || expectedChange
        ""   | null  || 0
        null | ""    || 0
        null | " "   || 0
        " "  | "   " || 0
        ""   | ""    || 0
        null | null  || 0
        null | "z"   || 1
    }


    /**
     * This comparator replicates NullAsBlankStringComparator but does *not* set
     * the handlesNulls flag and hence its null-handing logic
     * should be effectively overruled
     */
    class AnotherStringComparator extends NullAsBlankStringComparator {
        @Override
        boolean handlesNulls() {
            return false
        }
    }

    @Unroll
    def "should handle nulls in CustomValueComparator if handlesNulls is false"() {

        given:
        def javers = JaversBuilder.javers()
                .registerValue(String, new AnotherStringComparator())
                .build()

        when:
        def diff = javers.compare(
                DummyUserWithValues.dummyUserWithPosition("1", left),
                DummyUserWithValues.dummyUserWithPosition("1", right))

        then:
        diff.changes.size() == expectedChange

        where:
        left | right || expectedChange
        ""   | null  || 1
        null | ""    || 1
        ""   | ""    || 0
        ""   | " "   || 0
        null | null  || 0
    }
}
