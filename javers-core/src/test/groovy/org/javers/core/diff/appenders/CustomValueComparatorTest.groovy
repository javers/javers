package org.javers.core.diff.appenders

import org.javers.core.JaversBuilder
import org.javers.core.model.DummyUserWithValues
import spock.lang.Specification
import spock.lang.Unroll

class CustomValueComparatorTest extends Specification {

    def "should use CustomValueComparator for built-in ValueTypes"(){
      given:
      def javers = JaversBuilder.javers()
              .registerValue(BigDecimal, {a, b -> a.intValue() == b.intValue()}).build()

      when:
      def diff = javers.compare( new DummyUserWithValues("1", 100.1),
                                 new DummyUserWithValues("1", 100.2) )

      then:
      diff.changes.size() == 0
    }

    private class SomeValue {
        BigDecimal v
    }

    @Unroll
    def "should use CustomValueComparator for user's ValueTypes"(){
        given:
        def javers = JaversBuilder.javers()
                .registerValue(BigDecimal, {a, b -> a.intValue() == b.intValue()}).build()

        when:
        def diff = javers.compare( new SomeValue(v:left), new SomeValue(v:right) )

        then:
        diff.changes.size() == expectedChange

        where:
        left  | right || expectedChange
        100.1 | 100.1 || 0
        100.1 | 100.2 || 0
        101.1 | 100.1 || 1
    }

    private class SomeValues {
        String[] stringArray
        List<String> stringList
        Map<String, String> stringMap
    }

    @Unroll
    def "should use CustomValueComparator for Values stored in Arrays, Lists and Maps" () {
        when:
        def javers = JaversBuilder.javers()
                .registerValue(String, {a, b -> a.size() == b.size()})
                .build()

        def lObject = new SomeValues( stringArray: [left].toArray(),
                                      stringList:  [left],
                                      stringMap:   [a : left] )

        def rObject = new SomeValues( stringArray: [right].toArray(),
                                      stringList:  [right],
                                      stringMap:   [a : right] )

        then:
        javers.compare(lObject, rObject).changes.size() == expectedChange

        where:
        left            | right          || expectedChange
        "abc"           | "cba"          || 0
        "abc"           | "a"            || 3
    }
}
