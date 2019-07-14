package org.javers.core.diff.custom

import org.javers.core.JaversBuilder
import spock.lang.Specification

class CustomBigDecimalComparatorTest extends Specification {

    class Entity {
        BigDecimal value
        List<BigDecimal> values
    }

    def "should compare BigDecimal properties with desired precision"(){
      given:
      def javers = JaversBuilder.javers()
             .registerCustomComparator(new CustomBigDecimalComparator(2), BigDecimal).build()

      expect:
      javers.compare(new Entity(value: 1.123), new Entity(value: 1.124)).changes.size() == 0
      javers.compare(new Entity(value: 1.12), new Entity(value: 1.13)).changes.size() == 1
    }

    def "should compare BigDecimal list items with desired precision "(){
        given:
        def javers = JaversBuilder.javers()
                .registerCustomComparator(new CustomBigDecimalComparator(2), BigDecimal).build()

        expect:
        javers.compare(new Entity(values: [1.123]), new Entity(values: [1.124])).changes.size() == 0
        javers.compare(new Entity(values: [1.123]), new Entity(values: [1.124, 2])).changes.size() == 1
    }
}
