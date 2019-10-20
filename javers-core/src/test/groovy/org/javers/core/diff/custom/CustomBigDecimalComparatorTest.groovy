package org.javers.core.diff.custom

import org.javers.core.JaversBuilder
import spock.lang.Specification

class CustomBigDecimalComparatorTest extends Specification {

    class ValueObject {
        BigDecimal value
        List<BigDecimal> values
    }

    def "should compare BigDecimal properties with desired precision"(){
      given:
      def javers = JaversBuilder.javers()
             .registerValue(BigDecimal, new CustomBigDecimalComparator(2)).build()

      expect:
      javers.compare(new ValueObject(value: 1.123), new ValueObject(value: 1.124)).changes.size() == 0
      javers.compare(new ValueObject(value: 1.12), new ValueObject(value: 1.13)).changes.size() == 1
    }

    def "should compare BigDecimal lists with desired precision "(){
        given:
        def javers = JaversBuilder.javers()
                .registerValue(BigDecimal, new CustomBigDecimalComparator(2)).build()

        expect:
        javers.compare(new ValueObject(values: [1.123]), new ValueObject(values: [1.124])).changes.size() == 0
        javers.compare(new ValueObject(values: [1.123]), new ValueObject(values: [1.124, 2])).changes.size() == 1
    }

    def "should compare BigDecimal with fixed equals"() {
        when:
        def javers = JaversBuilder.javers()
                .registerValue(BigDecimal, {a, b -> a.compareTo(b) == 0},
                                           {a -> a.stripTrailingZeros().toString()})
                .build()

        then:
        javers.compare(new ValueObject(value: 1.000), new ValueObject(value: 1.00)).changes.size() == 0
        javers.compare(new ValueObject(value: 1.100), new ValueObject(value: 1.20)).changes.size() == 1

        when:
        javers = JaversBuilder.javers()
                .registerValue(BigDecimal, new BigDecimalComparatorWithFixedEquals())
                .build()

        then:
        javers.compare(new ValueObject(value: 1.000), new ValueObject(value: 1.00)).changes.size() == 0
        javers.compare(new ValueObject(value: 1.100), new ValueObject(value: 1.20)).changes.size() == 1
    }
}
