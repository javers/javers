package org.javers.core.diff.appenders

import com.google.common.base.Strings
import org.javers.core.JaversBuilder
import org.javers.core.diff.custom.CustomEmptyStringComparator
import org.javers.core.diff.custom.CustomValueComparator
import org.javers.core.model.DummyUserWithValues
import spock.lang.Specification
import spock.lang.Unroll

class NullHandlingCustomValueComparatorTest extends Specification {

    @Unroll
    def "should treat empty strings and nulls as equal if CustomEmptyStringComparator is used"() {

        given:
        def javers = JaversBuilder.javers()
                .registerValue(String, new CustomEmptyStringComparator()).build()

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
        ""   | ""    || 0
        null | null  || 0
    }

    @Unroll
    def "should handle nulls in CustomValueComparatorNullSafe if handlesNulls is false"() {

        // This comparator replicates CustomEmptyStringComparator but does *not* set
        // the handlesNulls flag and hence should be effectively overruled
        CustomValueComparator<String> comparator = new CustomValueComparator<String>() {

            @Override
            boolean equals(String a, String b) {
                return Objects.equals(Strings.emptyToNull(a), Strings.emptyToNull(b))
            }

            @Override
            String toString(String value) {
                return Strings.nullToEmpty(value)
            }
        }

        given:
        def javers = JaversBuilder.javers()
                .registerValue(String, comparator).build()

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
        null | null  || 0
    }

}
