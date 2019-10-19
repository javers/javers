package org.javers.core.diff.custom

import org.javers.core.JaversBuilder
import spock.lang.Specification
import spock.lang.Unroll

class CustomBigDecimalComparatorTest extends Specification {

    class Entity {
        BigDecimal value
        List<BigDecimal> values
    }

    def "should compare BigDecimal properties with desired precision"(){
      given:
      def javers = JaversBuilder.javers()
             .registerValue(BigDecimal, new CustomBigDecimalComparator(2)).build()

      expect:
      javers.compare(new Entity(value: 1.123), new Entity(value: 1.124)).changes.size() == 0
      javers.compare(new Entity(value: 1.12), new Entity(value: 1.13)).changes.size() == 1
    }

    def "should compare BigDecimal list items with desired precision "(){
        given:
        def javers = JaversBuilder.javers()
                .registerValue(BigDecimal, new CustomBigDecimalComparator(2)).build()

        expect:
        javers.compare(new Entity(values: [1.123]), new Entity(values: [1.124])).changes.size() == 0
        javers.compare(new Entity(values: [1.123]), new Entity(values: [1.124, 2])).changes.size() == 1
    }

    def "should compare BigDecimal with fixed equals"() {
        when:
        def javers = JaversBuilder.javers()
                .registerValue(BigDecimal, {a, b -> a.compareTo(b) == 0},
                                           {a -> a.stripTrailingZeros().toString()})
                .build()

        then:
        javers.compare(new Entity(value: 1.000), new Entity(value: 1.00)).changes.size() == 0
        javers.compare(new Entity(value: 1.100), new Entity(value: 1.20)).changes.size() == 1

        when:
        javers = JaversBuilder.javers()
                .registerValue(BigDecimal, new BigDecimalComparatorWithFixedEquals())
                .build()

        then:
        javers.compare(new Entity(value: 1.000), new Entity(value: 1.00)).changes.size() == 0
        javers.compare(new Entity(value: 1.100), new Entity(value: 1.20)).changes.size() == 1
    }
}
